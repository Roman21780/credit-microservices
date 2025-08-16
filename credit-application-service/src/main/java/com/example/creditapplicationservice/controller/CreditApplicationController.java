package com.example.creditapplicationservice.controller;

import com.example.creditapplicationservice.dto.CreditApplicationDto;
import com.example.creditapplicationservice.exception.ApplicationNotFoundException;
import com.example.creditapplicationservice.service.CreditApplicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/credit-applications")
@RequiredArgsConstructor
public class CreditApplicationController {
    private final CreditApplicationService service;

    @PostMapping
    public ResponseEntity<UUID> createApplication(@RequestBody CreditApplicationDto dto) {
        return ResponseEntity.ok(service.processApplication(dto));
    }

    @GetMapping("/{id}/status")
    public ResponseEntity<?> getStatus(@PathVariable UUID id) {
        try {
            return ResponseEntity.ok(service.getStatus(id));
        } catch (ApplicationNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error getting status", e);
            return ResponseEntity.internalServerError()
                    .body("Error processing request");
        }
    }
}
