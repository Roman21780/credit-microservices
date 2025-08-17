package com.example.creditapplicationservice.service;

import com.example.creditcommon.event.ApplicationEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaProducerService {
    private final KafkaTemplate<String, ApplicationEvent> kafkaTemplate;

    public void sendApplicationEvent(String s, ApplicationEvent event) {
        kafkaTemplate.send("credit-applications", event);
    }
}
