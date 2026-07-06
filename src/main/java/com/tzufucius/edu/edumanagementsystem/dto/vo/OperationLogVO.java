package com.tzufucius.edu.edumanagementsystem.dto.vo;

import java.time.LocalDateTime;

public record OperationLogVO(Long id, Long userId, String username, String moduleName, String operationType,
                             String targetTable, Long targetId, String description, String ipAddress,
                             LocalDateTime createdAt) {
}
