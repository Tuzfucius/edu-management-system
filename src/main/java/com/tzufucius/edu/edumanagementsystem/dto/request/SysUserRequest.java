package com.tzufucius.edu.edumanagementsystem.dto.request;

import jakarta.validation.constraints.NotBlank;

public record SysUserRequest(
        @NotBlank String username,
        String password,
        @NotBlank String role,
        Integer status
) {
}
