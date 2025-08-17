package com.example.creditapplicationservice.service;

import com.example.creditapplicationservice.dto.CreditApplicationDto;
import com.example.creditapplicationservice.exception.ApplicationNotFoundException;
import com.example.creditcommon.enums.ApplicationStatus;
import com.example.creditcommon.event.ApplicationEvent;
import com.example.creditapplicationservice.model.CreditApplication;
import com.example.creditapplicationservice.repository.CreditApplicationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreditApplicationService {
    private final CreditApplicationRepository repository;
    private final KafkaProducerService kafkaProducerService;

    @Transactional
    public UUID processApplication(CreditApplicationDto dto) {
        CreditApplication application = CreditApplication.builder()
                .amount(dto.getAmount())
                .term(dto.getTerm())
                .income(dto.getIncome())
                .currentDebt(dto.getCurrentDebt())
                .creditRating(dto.getCreditRating())
                .status(ApplicationStatus.IN_PROCESS)
                .build();

        CreditApplication saved = repository.save(application);
        log.info("Created new credit application with ID: {}", saved.getId());

        ApplicationEvent event = ApplicationEvent.builder()
                .applicationId(saved.getId())
                .amount(saved.getAmount())
                .term(saved.getTerm())
                .income(saved.getIncome())
                .currentDebt(saved.getCurrentDebt())
                .creditRating(saved.getCreditRating())
                .build();

        kafkaProducerService.sendApplicationEvent("credit-applications", event);
        return saved.getId();
    }

    public String getStatus(UUID id) {
        return repository.findStatusById(id)
                .orElseThrow(() -> new ApplicationNotFoundException(id));
    }
}