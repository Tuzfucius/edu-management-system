package com.tzufucius.edu.edumanagementsystem.dto.vo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public final class AcademicVOs {
    private AcademicVOs() {
    }

    public record SysUserVO(Long id, String username, String role, Integer status, LocalDateTime lastLoginAt,
                            LocalDateTime createdAt, LocalDateTime updatedAt) {
    }

    public record StudentVO(Long id, Long userId, Long classId, String studentNo, String studentName, String gender,
                            LocalDate birthDate, String phone, String email, Integer enrollmentYear, Integer status,
                            String className, String majorName, String collegeName, String username) {
    }

    public record TeacherVO(Long id, Long userId, Long departmentId, String teacherNo, String teacherName,
                            String gender, String title, String phone, String email, Integer status,
                            String departmentName, String collegeName, String username) {
    }

    public record TeachingTaskVO(Long id, Long courseId, Long teacherId, String semester, Integer weekday,
                                 Integer startSection, Integer endSection, String weeks, String classroom,
                                 Integer capacity, Integer selectedCount, Integer taskStatus, String courseCode,
                                 String courseName, BigDecimal credit, String teacherNo, String teacherName) {
    }

    public record StudentCourseVO(Long id, Long studentId, Long teachingTaskId, LocalDateTime selectTime,
                                  BigDecimal score, Integer gradeStatus, Integer status, String remark,
                                  String studentNo, String studentName, String courseCode, String courseName,
                                  BigDecimal credit, String teacherName, String semester, Integer weekday,
                                  Integer startSection, Integer endSection, String classroom) {
    }

    public record SelectableTaskVO(Long id, Long courseId, Long teacherId, String semester, Integer weekday,
                                   Integer startSection, Integer endSection, String weeks, String classroom,
                                   Integer capacity, Integer selectedCount, String courseCode, String courseName,
                                   BigDecimal credit, String teacherName, Integer selectedByMe,
                                   Long studentCourseId) {
    }

    public record TeacherStudentVO(Long id, Long teacherId, Long studentId, String guideType, LocalDate startDate,
                                   LocalDate endDate, Integer status, String teacherName, String studentNo,
                                   String studentName) {
    }

    public record OperationLogVO(Long id, Long userId, String username, String moduleName, String operationType,
                                 String targetTable, Long targetId, String description, String ipAddress,
                                 LocalDateTime createdAt) {
    }

    public record OverviewReportVO(Integer studentCount, Integer teacherCount, Integer courseCount,
                                   Integer teachingTaskCount, Integer selectionCount) {
    }

    public record NameValueReportVO(String name, Integer value) {
    }

    public record TeachingLoadReportVO(String teacherName, Integer taskCount, Integer selectedCount) {
    }
}
