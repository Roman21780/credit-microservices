package com.example.creditprocessingservice.service;

import com.example.creditcommon.enums.ApplicationStatus;
import com.example.creditcommon.event.ProcessingResultEvent;
import com.example.creditcommon.event.ApplicationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaListenerService {
    private final RabbitTemplate rabbitTemplate;
    private static final BigDecimal YEARLY_RATE = new BigDecimal("0.12");
    private static final BigDecimal MAX_PAYMENT_RATIO = new BigDecimal("0.5");

    @KafkaListener(topics = "credit-applications", groupId = "credit-processing")
    public void processApplication(ApplicationEvent event) {
        try {
            validateApplicationEvent(event);
            log.info("Processing application: {}", event.getApplicationId());

            BigDecimal monthlyPayment = calculateMonthlyPayment(event.getAmount(), event.getTerm());
            boolean approved = isApplicationApproved(event, monthlyPayment);

            ProcessingResultEvent result = ProcessingResultEvent.builder()
                    .applicationId(event.getApplicationId())
                    .status(approved ? ApplicationStatus.APPROVED : ApplicationStatus.REJECTED)
                    .monthlyPayment(approved ? monthlyPayment : BigDecimal.ZERO)
                    .build();

            rabbitTemplate.convertAndSend("credit-responses", result);
            log.info("Sent processing result for {}", event.getApplicationId());

        } catch (Exception e) {
            log.error("Error processing application {}", event.getApplicationId(), e);
            throw new RuntimeException("Application processing failed", e);
        }
    }

    private void validateApplicationEvent(ApplicationEvent event) {
        if (event.getApplicationId() == null) {
            throw new IllegalArgumentException("Application ID cannot be null");
        }
        if (event.getAmount() == null || event.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        if (event.getTerm() == null || event.getTerm() <= 0) {
            throw new IllegalArgumentException("Term must be positive");
        }
        if (event.getIncome() == null || event.getIncome().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Income must be positive");
        }
    }

    private boolean isApplicationApproved(ApplicationEvent event, BigDecimal monthlyPayment) {
        BigDecimal maxAllowedPayment = event.getIncome().multiply(MAX_PAYMENT_RATIO);
        return monthlyPayment.compareTo(maxAllowedPayment) <= 0;
    }

    private BigDecimal calculateMonthlyPayment(BigDecimal amount, Integer termMonths) {
        BigDecimal monthlyRate = YEARLY_RATE.divide(BigDecimal.valueOf(12), 10, RoundingMode.HALF_UP);
        BigDecimal ratePlusOne = monthlyRate.add(BigDecimal.ONE);
        BigDecimal pow = ratePlusOne.pow(termMonths);

        return amount.multiply(monthlyRate)
                .multiply(pow)
                .divide(pow.subtract(BigDecimal.ONE), 2, RoundingMode.HALF_UP);
    }
}