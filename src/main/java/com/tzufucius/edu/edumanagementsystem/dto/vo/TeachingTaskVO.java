package com.tzufucius.edu.edumanagementsystem.dto.vo;

import java.math.BigDecimal;

public record TeachingTaskVO(Long id, Long courseId, Long teacherId, String semester, Integer weekday,
                             Integer startSection, Integer endSection, String weeks, String classroom,
                             Integer capacity, Integer selectedCount, Integer taskStatus, String courseCode,
                             String courseName, BigDecimal credit, String teacherNo, String teacherName) {
}
