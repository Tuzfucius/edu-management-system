package com.tzufucius.edu.edumanagementsystem.dto.request;

import jakarta.validation.constraints.NotNull;

public record StudentCourseSelectRequest(
        @NotNull Long studentId,
        @NotNull Long teachingTaskId
) {
}
