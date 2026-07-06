package com.tzufucius.edu.edumanagementsystem.dto.vo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record StudentCourseVO(Long id, Long studentId, Long teachingTaskId, LocalDateTime selectTime,
                              BigDecimal score, Integer gradeStatus, Integer status, String remark,
                              String studentNo, String studentName, String courseCode, String courseName,
                              BigDecimal credit, String teacherName, String semester, Integer weekday,
                              Integer startSection, Integer endSection, String classroom) {
}
