package com.tzufucius.edu.edumanagementsystem.dto.vo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public final class BasicVOs {
    private BasicVOs() {
    }

    public record CollegeVO(Long id, String collegeCode, String collegeName, String description, Integer status,
                            LocalDateTime createdAt, LocalDateTime updatedAt) {
    }

    public record DepartmentVO(Long id, Long collegeId, String departmentCode, String departmentName,
                               String officeLocation, Integer status, LocalDateTime createdAt,
                               LocalDateTime updatedAt) {
    }

    public record MajorVO(Long id, Long collegeId, String majorCode, String majorName, Integer schoolingYears,
                          String degreeType, Integer status, LocalDateTime createdAt, LocalDateTime updatedAt) {
    }

    public record ClassInfoVO(Long id, Long majorId, String classCode, String className, Integer entranceYear,
                              Integer status, LocalDateTime createdAt, LocalDateTime updatedAt) {
    }

    public record CourseVO(Long id, String courseCode, String courseName, BigDecimal credit, Integer totalHours,
                           String courseType, String examType, Integer status, LocalDateTime createdAt,
                           LocalDateTime updatedAt) {
    }
}
