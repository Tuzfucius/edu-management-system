package com.tzufucius.edu.edumanagementsystem.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TeachingTaskRequest(
        @NotNull Long courseId,
        @NotNull Long teacherId,
        @NotBlank String semester,
        @NotNull @Min(1) @Max(7) Integer weekday,
        @NotNull @Min(1) Integer startSection,
        @NotNull @Min(1) Integer endSection,
        String weeks,
        String classroom,
        @Min(0) Integer capacity,
        Integer taskStatus
) {
}
