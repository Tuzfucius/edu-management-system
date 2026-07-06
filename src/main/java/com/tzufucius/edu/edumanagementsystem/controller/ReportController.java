package com.tzufucius.edu.edumanagementsystem.controller;

import com.tzufucius.edu.edumanagementsystem.common.Result;
import com.tzufucius.edu.edumanagementsystem.dto.vo.NameValueReportVO;
import com.tzufucius.edu.edumanagementsystem.dto.vo.OverviewReportVO;
import com.tzufucius.edu.edumanagementsystem.dto.vo.TeachingLoadReportVO;
import com.tzufucius.edu.edumanagementsystem.service.AcademicBusinessService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ReportController {
    private final AcademicBusinessService service;

    public ReportController(AcademicBusinessService service) {
        this.service = service;
    }

    @GetMapping("/overview")
    public Result<OverviewReportVO> overview() {
        return Result.success(service.overviewReport());
    }

    @GetMapping("/college-students")
    public Result<List<NameValueReportVO>> collegeStudents() {
        return Result.success(service.collegeStudentReport());
    }

    @GetMapping("/grade-distribution")
    public Result<List<NameValueReportVO>> gradeDistribution() {
        return Result.success(service.gradeDistributionReport());
    }

    @GetMapping("/teaching-load")
    public Result<List<TeachingLoadReportVO>> teachingLoad() {
        return Result.success(service.teachingLoadReport());
    }
}
