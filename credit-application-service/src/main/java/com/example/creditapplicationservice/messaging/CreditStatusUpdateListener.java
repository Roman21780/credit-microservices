package com.example.creditapplicationservice.messaging;

import com.example.creditapplicationservice.repository.CreditApplicationRepository;
import com.example.creditcommon.event.ProcessingResultEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Component
@RequiredArgsConstructor
public class CreditStatusUpdateListener {
    private final CreditApplicationRepository repository;

    @RabbitListener(queues = "${rabbitmq.queue.credit-responses}")
    @Transactional
    public void updateStatus(ProcessingResultEvent result) {
        try {
            log.info("Received status update for {}: {}",
                    result.getApplicationId(), result.getStatus());

            int updated = repository.updateStatus(
                    result.getApplicationId(),
                    result.getStatus().name()
            );

            if (updated == 0) {
                log.error("No application found with ID: {}", result.getApplicationId());
            } else {
                log.info("Successfully updated status for {}", result.getApplicationId());
            }
        } catch (Exception e) {
            log.error("Failed to update status for {}", result.getApplicationId(), e);
        }
    }
}