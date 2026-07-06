package com.tzufucius.edu.edumanagementsystem.dto.vo;

import java.time.LocalDateTime;

public record SysUserVO(Long id, String username, String role, Integer status, LocalDateTime lastLoginAt,
                        LocalDateTime createdAt, LocalDateTime updatedAt) {
}
