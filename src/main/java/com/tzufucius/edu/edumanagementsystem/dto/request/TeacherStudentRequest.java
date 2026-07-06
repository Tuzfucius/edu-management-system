package com.tzufucius.edu.edumanagementsystem.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record TeacherStudentRequest(
        @NotNull Long teacherId,
        @NotNull Long studentId,
        @NotBlank String guideType,
        LocalDate startDate,
        LocalDate endDate
) {
}
