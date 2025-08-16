package com.example.creditapplicationservice.messaging;

import com.example.creditapplicationservice.repository.CreditApplicationRepository;
import com.example.creditcommon.event.ProcessingResultEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CreditStatusUpdateListener {

    private final CreditApplicationRepository creditApplicationRepository;

    @RabbitListener(queues = "credit-responses")
    public void updateStatus(ProcessingResultEvent result) {
        try {
            creditApplicationRepository.updateStatus(
                    result.getApplicationId(),
                    result.getStatus()
            );
            log.info("Status updated for {}: {}", result.getApplicationId(), result.getStatus());
        } catch (Exception e) {
            log.error("Failed to update status for {}", result.getApplicationId(), e);
        }
    }
}