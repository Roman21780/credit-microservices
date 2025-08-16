package com.example.creditprocessingservice.controller;

import com.example.creditcommon.event.ApplicationEvent;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/credit-processing")
@RequiredArgsConstructor
public class CreditProcessingController {

    private final KafkaTemplate<String, ApplicationEvent> kafkaTemplate;

    @PostMapping("/process")
    public ResponseEntity<String> processApplication(@Valid @RequestBody ApplicationEvent request) {
        try {
            log.info("Sending application to Kafka: {}", request);
            kafkaTemplate.send("credit-applications", request);
            return ResponseEntity.ok("Application sent for processing: " + request.getApplicationId());
        } catch (Exception e) {
            log.error("Failed to process application", e);
            return ResponseEntity.internalServerError()
                    .body("Processing error: " + e.getMessage());
        }
    }
}
