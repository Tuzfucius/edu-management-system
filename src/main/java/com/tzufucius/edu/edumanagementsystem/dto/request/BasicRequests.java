package com.tzufucius.edu.edumanagementsystem.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public final class BasicRequests {
    private BasicRequests() {
    }

    public record CollegeRequest(
            @NotBlank String collegeCode,
            @NotBlank String collegeName,
            String description
    ) {
    }

    public record DepartmentRequest(
            @NotNull Long collegeId,
            @NotBlank String departmentCode,
            @NotBlank String departmentName,
            String officeLocation
    ) {
    }

    public record MajorRequest(
            @NotNull Long collegeId,
            @NotBlank String majorCode,
            @NotBlank String majorName,
            @NotNull @Min(1) Integer schoolingYears,
            String degreeType
    ) {
    }

    public record ClassInfoRequest(
            @NotNull Long majorId,
            @NotBlank String classCode,
            @NotBlank String className,
            @NotNull @Min(1900) Integer entranceYear
    ) {
    }

    public record CourseRequest(
            @NotBlank String courseCode,
            @NotBlank String courseName,
            @NotNull @DecimalMin(value = "0.0", inclusive = false) BigDecimal credit,
            @Min(1) Integer totalHours,
            String courseType,
            String examType
    ) {
    }
}
