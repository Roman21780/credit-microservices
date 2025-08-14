package com.example.creditprocessingservice.service;

import com.example.creditcommon.event.ProcessingResultEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import com.example.creditcommon.event.ApplicationEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaListenerService {
    private final RabbitTemplate rabbitTemplate;
    private static final BigDecimal YEARLY_RATE = new BigDecimal("0.12");

    @KafkaListener(topics = "credit-applications", groupId = "credit-processing")
    public void processApplication(ApplicationEvent event) {
        log.info("Processing application: {}", event.getApplicationId());

        BigDecimal monthlyPayment = calculateMonthlyPayment(
                event.getAmount(),
                event.getTerm(),
                YEARLY_RATE
        );

        BigDecimal totalDebt = event.getCurrentDebt().add(monthlyPayment.multiply(new BigDecimal(event.getTerm())));
        BigDecimal maxAllowedPayment = event.getIncome().multiply(new BigDecimal("0.5"));

        boolean approved = monthlyPayment.compareTo(maxAllowedPayment) <= 0;

        ProcessingResultEvent result = ProcessingResultEvent.builder()
                .applicationId(event.getApplicationId())
                .status(approved ? "APPROVED" : "REJECTED")
                .build();

        rabbitTemplate.convertAndSend("credit-responses", result);
        log.info("Decision for application {}: {}", event.getApplicationId(), result.getStatus());
    }

    private BigDecimal calculateMonthlyPayment(BigDecimal amount, Integer termMonths, BigDecimal yearlyRate) {
        BigDecimal monthlyRate = yearlyRate.divide(BigDecimal.valueOf(12), 10, RoundingMode.HALF_UP);
        BigDecimal ratePlusOne = monthlyRate.add(BigDecimal.ONE);
        BigDecimal pow = ratePlusOne.pow(termMonths);

        // Формула аннуитетного платежа: P = S * (r * (1 + r)^n) / ((1 + r)^n - 1)
        return amount.multiply(monthlyRate)
                .multiply(pow)
                .divide(pow.subtract(BigDecimal.ONE), 2, RoundingMode.HALF_UP);
    }
}
