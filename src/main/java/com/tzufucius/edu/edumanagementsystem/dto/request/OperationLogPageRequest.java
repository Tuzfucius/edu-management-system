package com.tzufucius.edu.edumanagementsystem.dto.request;

public record OperationLogPageRequest(
        String moduleName,
        String operationType,
        Long userId,
        String startTime,
        String endTime,
        Integer page,
        Integer size
) {
}
