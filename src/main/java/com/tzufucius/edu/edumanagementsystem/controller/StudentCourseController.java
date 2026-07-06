package com.tzufucius.edu.edumanagementsystem.controller;

import com.tzufucius.edu.edumanagementsystem.common.Result;
import com.tzufucius.edu.edumanagementsystem.dto.request.ScoreUpdateRequest;
import com.tzufucius.edu.edumanagementsystem.dto.request.StudentCourseSelectRequest;
import com.tzufucius.edu.edumanagementsystem.dto.vo.SelectableTaskVO;
import com.tzufucius.edu.edumanagementsystem.dto.vo.StudentCourseVO;
import com.tzufucius.edu.edumanagementsystem.service.AcademicBusinessService;
import com.tzufucius.edu.edumanagementsystem.service.OperationLogService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/student-courses")
public class StudentCourseController {
    private final AcademicBusinessService service;
    private final OperationLogService operationLogService;

    public StudentCourseController(AcademicBusinessService service, OperationLogService operationLogService) {
        this.service = service;
        this.operationLogService = operationLogService;
    }

    @GetMapping
    public Result<List<StudentCourseVO>> list(@RequestParam(required = false) Long studentId,
                                              @RequestParam(required = false) Long teacherId) {
        return Result.success(service.listStudentCourses(studentId, teacherId));
    }

    @GetMapping("/selectable")
    public Result<List<SelectableTaskVO>> selectable(@RequestParam Long studentId, @RequestParam String semester) {
        return Result.success(service.listSelectableTasks(studentId, semester));
    }

    @PostMapping
    public Result<Void> select(@Valid @RequestBody StudentCourseSelectRequest body, HttpSession session,
                               HttpServletRequest request) {
        Long studentCourseId = service.selectCourse(body);
        operationLogService.record(request, session, "选课管理", "SELECT_COURSE", "student_course", studentCourseId, "学生选课");
        return Result.success(null);
    }

    @DeleteMapping("/{id}")
    public Result<Void> drop(@PathVariable Long id, HttpSession session, HttpServletRequest request) {
        service.dropCourse(id);
        operationLogService.record(request, session, "选课管理", "DROP_COURSE", "student_course", id, "学生退课");
        return Result.success(null);
    }

    @PutMapping("/{id}/score")
    public Result<Void> updateScore(@PathVariable Long id, @Valid @RequestBody ScoreUpdateRequest body,
                                    HttpSession session, HttpServletRequest request) {
        service.updateScore(id, body);
        operationLogService.record(request, session, "成绩管理", "UPDATE_SCORE", "student_course", id, "成绩录入或修改");
        return Result.success(null);
    }
}
