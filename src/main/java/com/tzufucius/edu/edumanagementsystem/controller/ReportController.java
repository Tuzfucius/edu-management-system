package com.tzufucius.edu.edumanagementsystem.controller;

import com.tzufucius.edu.edumanagementsystem.common.Result;
import com.tzufucius.edu.edumanagementsystem.service.AcademicBusinessService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
public class ReportController {
    private final AcademicBusinessService service;

    public ReportController(AcademicBusinessService service) {
        this.service = service;
    }

    @GetMapping("/overview")
    public Result<Map<String, Object>> overview() {
        return Result.success(service.overviewReport());
    }

    @GetMapping("/college-students")
    public Result<List<Map<String, Object>>> collegeStudents() {
        return Result.success(service.collegeStudentReport());
    }

    @GetMapping("/grade-distribution")
    public Result<List<Map<String, Object>>> gradeDistribution() {
        return Result.success(service.gradeDistributionReport());
    }

    @GetMapping("/teaching-load")
    public Result<List<Map<String, Object>>> teachingLoad() {
        return Result.success(service.teachingLoadReport());
    }
}
