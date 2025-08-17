package com.example.creditcommon.event;

import com.example.creditcommon.enums.ApplicationStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessingResultEvent {
    private UUID applicationId;
    private ApplicationStatus status;
    private BigDecimal monthlyPayment;
    private LocalDateTime decisionDate;
    private String rejectionReason;
}