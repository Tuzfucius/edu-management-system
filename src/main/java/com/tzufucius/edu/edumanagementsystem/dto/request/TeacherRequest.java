package com.tzufucius.edu.edumanagementsystem.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TeacherRequest(
        Long userId,
        @Valid SysUserRequest account,
        @NotNull Long departmentId,
        @NotBlank String teacherNo,
        @NotBlank String teacherName,
        String gender,
        String title,
        String phone,
        String email
) {
}
