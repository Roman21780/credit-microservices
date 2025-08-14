package com.example.creditapplicationservice.service;

import com.example.creditapplicationservice.dto.CreditApplicationDto;
import com.example.creditapplicationservice.event.ApplicationEvent;
import com.example.creditapplicationservice.model.CreditApplication;
import com.example.creditapplicationservice.repository.CreditApplicationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreditApplicationService {
    private final CreditApplicationRepository repository;
    private final KafkaProducerService kafkaProducerService;

    @Transactional
    public Long processApplication(CreditApplicationDto dto) {
        // Конвертируем DTO в сущность
        CreditApplication application = CreditApplication.builder()
                .amount(dto.getAmount())
                .term(dto.getTerm())
                .income(dto.getIncome())
                .currentDebt(dto.getCurrentDebt())
                .creditRating(dto.getCreditRating())
                .build();

        // Сохраняем в БД
        CreditApplication savedApplication = repository.save(application);

        // Отправляем событие в Kafka
        ApplicationEvent event = ApplicationEvent.builder()
                .applicationId(savedApplication.getId())
                .amount(savedApplication.getAmount())
                .term(savedApplication.getTerm())
                .income(savedApplication.getIncome())
                .currentDebt(savedApplication.getCurrentDebt())
                .creditRating(savedApplication.getCreditRating())
                .build();

        kafkaProducerService.sendApplicationEvent(event);

        return savedApplication.getId();
    }

    public String getStatus(Long id) {
        return repository.findById(id)
                .map(CreditApplication::getStatus)
                .orElseThrow(() -> new RuntimeException("Application not found with id:" + id));
    }
}
