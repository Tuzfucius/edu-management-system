package com.tzufucius.edu.edumanagementsystem.dto.vo;

import java.time.LocalDateTime;

public record DepartmentVO(Long id, Long collegeId, String departmentCode, String departmentName,
                           String officeLocation, Integer status, LocalDateTime createdAt,
                           LocalDateTime updatedAt) {
}
