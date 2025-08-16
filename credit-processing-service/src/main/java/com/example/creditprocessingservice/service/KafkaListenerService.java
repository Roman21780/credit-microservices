package com.example.creditprocessingservice.service;

import com.example.creditcommon.event.ProcessingResultEvent;
import com.example.creditcommon.event.ApplicationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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
            log.info("Processing application: {}", event);

            BigDecimal monthlyPayment = calculateMonthlyPayment(event.getAmount(), event.getTerm());
            boolean approved = isApplicationApproved(event, monthlyPayment);

            ProcessingResultEvent result = buildProcessingResult(event, approved);
            sendProcessingResult(result);

        } catch (Exception e) {
            log.error("Error processing application {}", event.getApplicationId(), e);
            throw new RuntimeException("Application processing failed", e);
        }
    }

    private void validateApplicationEvent(ApplicationEvent event) {
        if (!StringUtils.hasText(event.getApplicationId())) {
            throw new IllegalArgumentException("Application ID is required");
        }
        if (event.getAmount() == null || event.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Invalid amount");
        }
        if (event.getTerm() == null || event.getTerm() <= 0) {
            throw new IllegalArgumentException("Invalid term");
        }
    }

    private boolean isApplicationApproved(ApplicationEvent event, BigDecimal monthlyPayment) {
        BigDecimal totalDebt = event.getCurrentDebt().add(
                monthlyPayment.multiply(BigDecimal.valueOf(event.getTerm()))
        );
        BigDecimal maxAllowedPayment = event.getIncome().multiply(MAX_PAYMENT_RATIO);
        return monthlyPayment.compareTo(maxAllowedPayment) <= 0;
    }

    private ProcessingResultEvent buildProcessingResult(ApplicationEvent event, boolean approved) {
        return ProcessingResultEvent.builder()
                .applicationId(event.getApplicationId())
                .status(approved ? "APPROVED" : "REJECTED")
                .monthlyPayment(approved ? calculateMonthlyPayment(event.getAmount(), event.getTerm()) : BigDecimal.ZERO)
                .build();
    }

    private void sendProcessingResult(ProcessingResultEvent result) {
        try {
            rabbitTemplate.convertAndSend("credit-responses", result);
            log.info("Successfully sent processing result: {}", result);
        } catch (Exception e) {
            log.error("Failed to send processing result to RabbitMQ", e);
            throw new RuntimeException("RabbitMQ communication error", e);
        }
    }

    // Формула аннуитетного платежа: P = S * (r * (1 + r)^n) / ((1 + r)^n - 1)
    private BigDecimal calculateMonthlyPayment(BigDecimal amount, Integer termMonths) {
        BigDecimal monthlyRate = YEARLY_RATE.divide(BigDecimal.valueOf(12), 10, RoundingMode.HALF_UP);
        BigDecimal ratePlusOne = monthlyRate.add(BigDecimal.ONE);
        BigDecimal pow = ratePlusOne.pow(termMonths);

        return amount.multiply(monthlyRate)
                .multiply(pow)
                .divide(pow.subtract(BigDecimal.ONE), 2, RoundingMode.HALF_UP);
    }
}
