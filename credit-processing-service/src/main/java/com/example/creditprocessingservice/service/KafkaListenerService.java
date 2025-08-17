package com.example.creditprocessingservice.service;

import com.example.creditcommon.enums.ApplicationStatus;
import com.example.creditcommon.event.ProcessingResultEvent;
import com.example.creditcommon.event.ApplicationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaListenerService {
    private static final Integer MIN_CREDIT_RATING = 600;
    private final RabbitTemplate rabbitTemplate;
    private static final BigDecimal YEARLY_RATE = new BigDecimal("0.12");
    private static final BigDecimal MAX_PAYMENT_RATIO = new BigDecimal("0.5");

    @KafkaListener(topics = "credit-applications", groupId = "credit-processing")
    @Transactional
    public void processApplication(ApplicationEvent event) {
        try {
            validateApplication(event); // Используем встроенную валидацию

            log.info("Processing application: {}", event.getApplicationId());

            BigDecimal monthlyPayment = calculateMonthlyPayment(event.getAmount(), event.getTerm());
            boolean approved = isApplicationApproved(event, monthlyPayment);

            ProcessingResultEvent result = ProcessingResultEvent.builder()
                    .applicationId(event.getApplicationId())
                    .status(approved ? ApplicationStatus.APPROVED : ApplicationStatus.REJECTED)
                    .monthlyPayment(approved ? monthlyPayment : BigDecimal.ZERO)
                    .decisionDate(LocalDateTime.now())
                    .rejectionReason(approved ? null : "Monthly payment exceeds 50% of income")
                    .build();

            rabbitTemplate.convertAndSend("credit-responses", result);
            log.info("Status updated for {}: {}", event.getApplicationId(), result.getStatus());

        } catch (IllegalArgumentException e) {
            log.error("Validation error for application {}: {}",
                    event.getApplicationId(), e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error processing application {}", event.getApplicationId(), e);
            throw new RuntimeException("Application processing failed", e);
        }
    }

    private void validateApplication(ApplicationEvent event) {
        if (event.getApplicationId() == null) {
            throw new IllegalArgumentException("Application ID cannot be null");
        }
        if (event.getAmount() == null || event.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        if (event.getTerm() == null || event.getTerm() <= 0) {
            throw new IllegalArgumentException("Term must be positive (months)");
        }
        if (event.getIncome() == null || event.getIncome().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Income must be positive");
        }
        if (event.getCurrentDebt() == null || event.getCurrentDebt().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Current debt cannot be negative");
        }
        if (event.getCreditRating() == null || event.getCreditRating() < 300 || event.getCreditRating() > 850) {
            throw new IllegalArgumentException("Credit rating must be between 300 and 850");
        }
    }

    private boolean isApplicationApproved(ApplicationEvent event, BigDecimal monthlyPayment) {
        if (event.getCreditRating() < MIN_CREDIT_RATING) {
            return false;
        }
        BigDecimal maxAllowedPayment = event.getIncome().multiply(MAX_PAYMENT_RATIO);
        return monthlyPayment.compareTo(maxAllowedPayment) <= 0;
    }

    private BigDecimal calculateMonthlyPayment(BigDecimal amount, Integer termMonths) {
        if (amount == null || termMonths == null || termMonths <= 0) {
            throw new IllegalArgumentException("Invalid parameters for payment calculation");
        }

        BigDecimal monthlyRate = YEARLY_RATE.divide(BigDecimal.valueOf(12), 10, RoundingMode.HALF_UP);
        BigDecimal ratePlusOne = monthlyRate.add(BigDecimal.ONE);
        BigDecimal pow = ratePlusOne.pow(termMonths);

        return amount.multiply(monthlyRate)
                .multiply(pow)
                .divide(pow.subtract(BigDecimal.ONE), 2, RoundingMode.HALF_UP);
    }
}