package com.tzufucius.edu.edumanagementsystem.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ScoreUpdateRequest(
        @NotNull @DecimalMin("0.00") @DecimalMax("100.00") BigDecimal score,
        String remark
) {
}
