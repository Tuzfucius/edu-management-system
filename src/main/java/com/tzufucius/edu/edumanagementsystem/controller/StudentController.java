package com.tzufucius.edu.edumanagementsystem.controller;

import com.tzufucius.edu.edumanagementsystem.auth.AuthContext;
import com.tzufucius.edu.edumanagementsystem.auth.RequireRole;
import com.tzufucius.edu.edumanagementsystem.common.Result;
import com.tzufucius.edu.edumanagementsystem.dto.request.StudentRequest;
import com.tzufucius.edu.edumanagementsystem.dto.vo.StudentVO;
import com.tzufucius.edu.edumanagementsystem.service.AcademicBusinessService;
import com.tzufucius.edu.edumanagementsystem.service.OperationLogService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
@RequireRole("ADMIN")
public class StudentController {
    private final AcademicBusinessService service;
    private final OperationLogService operationLogService;

    public StudentController(AcademicBusinessService service, OperationLogService operationLogService) {
        this.service = service;
        this.operationLogService = operationLogService;
    }

    @GetMapping
    public Result<List<StudentVO>> list() {
        return Result.success(service.listStudents());
    }

    @GetMapping("/{id}")
    public Result<StudentVO> get(@PathVariable Long id) {
        return Result.success(service.getStudent(id));
    }

    @GetMapping("/by-user/{userId}")
    @RequireRole({"ADMIN", "STUDENT"})
    public Result<StudentVO> getByUser(@PathVariable Long userId, HttpSession session) {
        return Result.success(service.getStudentByUserId(userId, AuthContext.requireCurrentUser(session)));
    }

    @PostMapping
    public Result<Void> create(@Valid @RequestBody StudentRequest body, HttpSession session, HttpServletRequest request) {
        service.createStudent(body);
        operationLogService.record(request, session, "学生管理", "INSERT", "student", null, "新增学生：" + body.studentName());
        return Result.success(null);
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody StudentRequest body, HttpSession session,
                               HttpServletRequest request) {
        service.updateStudent(id, body);
        operationLogService.record(request, session, "学生管理", "UPDATE", "student", id, "修改学生：" + body.studentName());
        return Result.success(null);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id, HttpSession session, HttpServletRequest request) {
        service.deleteStudent(id);
        operationLogService.record(request, session, "学生管理", "DISABLE", "student", id, "停用学生");
        return Result.success(null);
    }
}
