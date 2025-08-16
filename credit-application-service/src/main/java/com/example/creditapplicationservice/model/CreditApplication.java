package com.example.creditapplicationservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "credit_applications")
public class CreditApplication {

    public enum Status {
        IN_PROCESS, APPROVED, REJECTED
    }

    @Id
    @GeneratedValue
    @Column(columnDefinition = "UUID")
    private UUID id;

    @Column(name = "application_id", unique = true)
    private String applicationId;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private Integer term;

    @Column(nullable = false)
    private BigDecimal income;

    @Column(name = "current_debt", nullable = false)
    private BigDecimal currentDebt;

    @Column(name = "credit_rating", nullable = false)
    private Integer creditRating;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Status status = Status.IN_PROCESS;

    @Column(name = "created_at")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}