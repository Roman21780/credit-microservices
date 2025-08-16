package com.example.creditapplicationservice.service;

import com.example.creditapplicationservice.dto.CreditApplicationDto;
import com.example.creditapplicationservice.exception.ApplicationNotFoundException;
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
                .status(CreditApplication.Status.IN_PROCESS) // Устанавливаем начальный статус
                .build();

        CreditApplication savedApplication = repository.save(application);
        log.info("Created new credit application with ID: {}", savedApplication.getId());

        ApplicationEvent event = ApplicationEvent.builder()
                .applicationId(savedApplication.getId()) // Преобразуем UUID в String
                .amount(savedApplication.getAmount())
                .term(savedApplication.getTerm())
                .income(savedApplication.getIncome())
                .currentDebt(savedApplication.getCurrentDebt())
                .creditRating(savedApplication.getCreditRating())
                .build();

        kafkaProducerService.sendApplicationEvent(event);
        return savedApplication.getId(); // Возвращаем UUID вместо String
    }

    public String getStatus(UUID id) {
        return repository.findStatusById(id)
                .orElseThrow(() -> new ApplicationNotFoundException(id));
    }
}