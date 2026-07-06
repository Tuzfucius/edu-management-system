package com.tzufucius.edu.edumanagementsystem.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ClassInfoRequest(
        @NotNull Long majorId,
        @NotBlank String classCode,
        @NotBlank String className,
        @NotNull @Min(1900) Integer entranceYear
) {
}
