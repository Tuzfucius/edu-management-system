package com.tzufucius.edu.edumanagementsystem.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record StudentRequest(
        Long userId,
        @Valid SysUserRequest account,
        @NotNull Long classId,
        @NotBlank String studentNo,
        @NotBlank String studentName,
        String gender,
        LocalDate birthDate,
        String phone,
        String email,
        @NotNull @Min(1900) Integer enrollmentYear
) {
}
