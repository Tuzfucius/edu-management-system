package com.tzufucius.edu.edumanagementsystem.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public final class AcademicRequests {
    private AcademicRequests() {
    }

    public record SysUserRequest(
            @NotBlank String username,
            String password,
            @NotBlank String role,
            Integer status
    ) {
    }

    public record StudentRequest(
            Long userId,
            @Valid SysUserRequest account,
            @NotNull Long classId,
            @NotBlank String studentNo,
            @NotBlank String studentName,
            String gender,
            LocalDate birthDate,
            String phone,
            String email,
            @NotNull @Min(1900) Integer enrollmentYear
    ) {
    }

    public record TeacherRequest(
            Long userId,
            @Valid SysUserRequest account,
            @NotNull Long departmentId,
            @NotBlank String teacherNo,
            @NotBlank String teacherName,
            String gender,
            String title,
            String phone,
            String email
    ) {
    }

    public record TeachingTaskRequest(
            @NotNull Long courseId,
            @NotNull Long teacherId,
            @NotBlank String semester,
            @NotNull @Min(1) @Max(7) Integer weekday,
            @NotNull @Min(1) Integer startSection,
            @NotNull @Min(1) Integer endSection,
            String weeks,
            String classroom,
            @Min(0) Integer capacity,
            Integer taskStatus
    ) {
    }

    public record StudentCourseSelectRequest(
            @NotNull Long studentId,
            @NotNull Long teachingTaskId
    ) {
    }

    public record ScoreUpdateRequest(
            @NotNull @DecimalMin("0.00") @DecimalMax("100.00") BigDecimal score,
            String remark
    ) {
    }

    public record TeacherStudentRequest(
            @NotNull Long teacherId,
            @NotNull Long studentId,
            @NotBlank String guideType,
            LocalDate startDate,
            LocalDate endDate
    ) {
    }

    public record OperationLogPageRequest(
            String moduleName,
            String operationType,
            Long userId,
            String startTime,
            String endTime,
            Integer page,
            Integer size
    ) {
    }
}
