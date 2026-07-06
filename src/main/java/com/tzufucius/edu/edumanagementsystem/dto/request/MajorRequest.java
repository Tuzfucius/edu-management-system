package com.tzufucius.edu.edumanagementsystem.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MajorRequest(
        @NotNull Long collegeId,
        @NotBlank String majorCode,
        @NotBlank String majorName,
        @NotNull @Min(1) Integer schoolingYears,
        String degreeType
) {
}
