package com.example.creditcommon.event;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationEvent {
    @NotNull
    private UUID applicationId;

    @Positive
    private BigDecimal amount;

    @Positive
    private Integer term;

    @Positive
    private BigDecimal income;

    @PositiveOrZero
    private BigDecimal currentDebt;

    @Min(300) @Max(850)
    private Integer creditRating;
}
