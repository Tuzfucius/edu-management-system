package com.tzufucius.edu.edumanagementsystem.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CourseRequest(
        @NotBlank String courseCode,
        @NotBlank String courseName,
        @NotNull @DecimalMin(value = "0.0", inclusive = false) BigDecimal credit,
        @Min(1) Integer totalHours,
        String courseType,
        String examType
) {
}
