package com.tzufucius.edu.edumanagementsystem.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DepartmentRequest(
        @NotNull Long collegeId,
        @NotBlank String departmentCode,
        @NotBlank String departmentName,
        String officeLocation
) {
}
