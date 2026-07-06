package com.tzufucius.edu.edumanagementsystem.dto.vo;

import java.time.LocalDate;

public record StudentVO(Long id, Long userId, Long classId, String studentNo, String studentName, String gender,
                        LocalDate birthDate, String phone, String email, Integer enrollmentYear, Integer status,
                        String className, String majorName, String collegeName, String username) {
}
