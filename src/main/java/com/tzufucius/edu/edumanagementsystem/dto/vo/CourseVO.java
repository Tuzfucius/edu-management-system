package com.tzufucius.edu.edumanagementsystem.dto.vo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CourseVO(Long id, String courseCode, String courseName, BigDecimal credit, Integer totalHours,
                       String courseType, String examType, Integer status, LocalDateTime createdAt,
                       LocalDateTime updatedAt) {
}
