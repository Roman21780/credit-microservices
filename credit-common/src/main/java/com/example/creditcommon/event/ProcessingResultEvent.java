package com.example.creditcommon.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessingResultEvent {
    private String applicationId;
    private String status;
    private BigDecimal monthlyPayment;
}
