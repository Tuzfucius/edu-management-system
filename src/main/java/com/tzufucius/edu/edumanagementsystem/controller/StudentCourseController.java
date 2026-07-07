package com.tzufucius.edu.edumanagementsystem.controller;

import com.tzufucius.edu.edumanagementsystem.auth.AuthContext;
import com.tzufucius.edu.edumanagementsystem.auth.RequireRole;
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
@RequireRole({"ADMIN", "TEACHER", "STUDENT"})
public class StudentCourseController {
    private final AcademicBusinessService service;
    private final OperationLogService operationLogService;

    public StudentCourseController(AcademicBusinessService service, OperationLogService operationLogService) {
        this.service = service;
        this.operationLogService = operationLogService;
    }

    @GetMapping
    public Result<List<StudentCourseVO>> list(@RequestParam(required = false) Long studentId,
                                              @RequestParam(required = false) Long teacherId,
                                              HttpSession session) {
        return Result.success(service.listStudentCourses(studentId, teacherId, AuthContext.requireCurrentUser(session)));
    }

    @GetMapping("/selectable")
    @RequireRole({"ADMIN", "STUDENT"})
    public Result<List<SelectableTaskVO>> selectable(@RequestParam Long studentId, @RequestParam String semester,
                                                     HttpSession session) {
        return Result.success(service.listSelectableTasks(studentId, semester, AuthContext.requireCurrentUser(session)));
    }

    @PostMapping
    @RequireRole("STUDENT")
    public Result<Void> select(@Valid @RequestBody StudentCourseSelectRequest body, HttpSession session,
                               HttpServletRequest request) {
        Long studentCourseId = service.selectCourse(body, AuthContext.requireCurrentUser(session));
        operationLogService.record(request, session, "选课管理", "SELECT_COURSE", "student_course", studentCourseId, "学生选课");
        return Result.success(null);
    }

    @DeleteMapping("/{id}")
    @RequireRole({"ADMIN", "STUDENT"})
    public Result<Void> drop(@PathVariable Long id, HttpSession session, HttpServletRequest request) {
        service.dropCourse(id, AuthContext.requireCurrentUser(session));
        operationLogService.record(request, session, "选课管理", "DROP_COURSE", "student_course", id, "学生退课");
        return Result.success(null);
    }

    @PutMapping("/{id}/score")
    @RequireRole({"ADMIN", "TEACHER"})
    public Result<Void> updateScore(@PathVariable Long id, @Valid @RequestBody ScoreUpdateRequest body,
                                    HttpSession session, HttpServletRequest request) {
        service.updateScore(id, body, AuthContext.requireCurrentUser(session));
        operationLogService.record(request, session, "成绩管理", "UPDATE_SCORE", "student_course", id, "成绩录入或修改");
        return Result.success(null);
    }
    @DeleteMapping("/{id}/score")
    @RequireRole({"ADMIN", "TEACHER"})
    public Result<Void> revokeScore(@PathVariable Long id, HttpSession session, HttpServletRequest request) {
        service.revokeScore(id, AuthContext.requireCurrentUser(session));
        operationLogService.record(request, session, "成绩管理", "REVOKE_SCORE", "student_course", id, "撤销成绩");
        return Result.success(null);
    }
}
