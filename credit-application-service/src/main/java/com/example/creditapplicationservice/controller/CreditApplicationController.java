package com.example.creditapplicationservice.controller;

import com.example.creditapplicationservice.dto.CreditApplicationDto;
import com.example.creditapplicationservice.service.CreditApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/credit-applications")
@RequiredArgsConstructor
public class CreditApplicationController {
    private final CreditApplicationService service;

    @PostMapping
    public ResponseEntity<String> createApplication(@RequestBody CreditApplicationDto dto) {
        String id = service.processApplication(dto);
        return ResponseEntity.ok(id);
    }

    @GetMapping("/{id}/status")
    public ResponseEntity<String> getStatus(@PathVariable String id) {
        try {
            return ResponseEntity.ok(service.getStatus(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
