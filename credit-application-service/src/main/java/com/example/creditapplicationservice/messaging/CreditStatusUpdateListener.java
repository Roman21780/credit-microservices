package com.example.creditapplicationservice.messaging;

import com.example.creditapplicationservice.model.CreditApplication;
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
        creditApplicationRepository.updateStatus(
                result.getApplicationId(),
                CreditApplication.Status.valueOf(String.valueOf(result.getStatus()))
        );
    }
}