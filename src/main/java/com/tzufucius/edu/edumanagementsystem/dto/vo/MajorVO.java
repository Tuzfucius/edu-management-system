package com.tzufucius.edu.edumanagementsystem.dto.vo;

import java.time.LocalDateTime;

public record MajorVO(Long id, Long collegeId, String majorCode, String majorName, Integer schoolingYears,
                      String degreeType, Integer status, LocalDateTime createdAt, LocalDateTime updatedAt) {
}
