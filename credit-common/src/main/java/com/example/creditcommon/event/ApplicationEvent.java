package com.example.creditcommon.event;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
public class ApplicationEvent {
    @NotBlank
    private String applicationId;

    @NotNull
    @Positive
    private BigDecimal amount;

    @NotNull
    @Min(1)
    private Integer term;

    @NotNull
    @Positive
    private BigDecimal income;

    @NotNull
    @PositiveOrZero
    private BigDecimal currentDebt;

    @NotNull
    private Integer creditRating;
}
