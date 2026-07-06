package com.tzufucius.edu.edumanagementsystem.dto.vo;

public record TeacherVO(Long id, Long userId, Long departmentId, String teacherNo, String teacherName,
                        String gender, String title, String phone, String email, Integer status,
                        String departmentName, String collegeName, String username) {
}
