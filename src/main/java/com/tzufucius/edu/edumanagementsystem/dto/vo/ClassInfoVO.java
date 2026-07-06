package com.tzufucius.edu.edumanagementsystem.dto.vo;

import java.time.LocalDateTime;

public record ClassInfoVO(Long id, Long majorId, String classCode, String className, Integer entranceYear,
                          Integer status, LocalDateTime createdAt, LocalDateTime updatedAt) {
}
