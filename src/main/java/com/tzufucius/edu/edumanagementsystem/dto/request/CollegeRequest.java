package com.tzufucius.edu.edumanagementsystem.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CollegeRequest(
        @NotBlank String collegeCode,
        @NotBlank String collegeName,
        String description
) {
}
