package com.tzufucius.edu.edumanagementsystem.dto.vo;

import java.time.LocalDate;

public record TeacherStudentVO(Long id, Long teacherId, Long studentId, String guideType, LocalDate startDate,
                               LocalDate endDate, Integer status, String teacherName, String studentNo,
                               String studentName) {
}
