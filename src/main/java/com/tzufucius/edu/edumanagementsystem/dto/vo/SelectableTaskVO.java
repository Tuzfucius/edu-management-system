package com.tzufucius.edu.edumanagementsystem.dto.vo;

import java.math.BigDecimal;

public record SelectableTaskVO(Long id, Long courseId, Long teacherId, String semester, Integer weekday,
                               Integer startSection, Integer endSection, String weeks, String classroom,
                               Integer capacity, Integer selectedCount, String courseCode, String courseName,
                               BigDecimal credit, String teacherName, Integer selectedByMe,
                               Long studentCourseId) {
}
