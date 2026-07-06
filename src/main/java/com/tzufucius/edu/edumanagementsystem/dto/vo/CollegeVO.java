package com.tzufucius.edu.edumanagementsystem.dto.vo;

import java.time.LocalDateTime;

public record CollegeVO(Long id, String collegeCode, String collegeName, String description, Integer status,
                        LocalDateTime createdAt, LocalDateTime updatedAt) {
}
