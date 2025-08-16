package com.example.creditapplicationservice.dto;

import com.example.creditapplicationservice.model.CreditApplication;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreditApplicationDto {
    private String id;
    private BigDecimal amount;
    private Integer term;
    private BigDecimal income;
    private BigDecimal currentDebt;
    private Integer creditRating;
    private CreditApplication.Status status;
}
