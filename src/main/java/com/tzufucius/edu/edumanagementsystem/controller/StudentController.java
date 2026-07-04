package com.tzufucius.edu.edumanagementsystem.controller;

import com.tzufucius.edu.edumanagementsystem.common.Result;
import com.tzufucius.edu.edumanagementsystem.service.AcademicBusinessService;
import com.tzufucius.edu.edumanagementsystem.service.OperationLogService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/students")
public class StudentController {
    private final AcademicBusinessService service;
    private final OperationLogService operationLogService;

    public StudentController(AcademicBusinessService service, OperationLogService operationLogService) {
        this.service = service;
        this.operationLogService = operationLogService;
    }

    @GetMapping
    public Result<List<Map<String, Object>>> list() {
        return Result.success(service.listStudents());
    }

    @GetMapping("/{id}")
    public Result<Map<String, Object>> get(@PathVariable Long id) {
        return Result.success(service.getStudent(id));
    }

    @GetMapping("/by-user/{userId}")
    public Result<Map<String, Object>> getByUser(@PathVariable Long userId) {
        return Result.success(service.getStudentByUserId(userId));
    }

    @PostMapping
    public Result<Void> create(@RequestBody Map<String, Object> body, HttpSession session, HttpServletRequest request) {
        service.createStudent(body);
        operationLogService.record(request, session, "学生管理", "INSERT", "student", null, "新增学生：" + body.get("studentName"));
        return Result.success(null);
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody Map<String, Object> body, HttpSession session, HttpServletRequest request) {
        service.updateStudent(id, body);
        operationLogService.record(request, session, "学生管理", "UPDATE", "student", id, "修改学生：" + body.get("studentName"));
        return Result.success(null);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id, HttpSession session, HttpServletRequest request) {
        service.deleteStudent(id);
        operationLogService.record(request, session, "学生管理", "DISABLE", "student", id, "停用学生");
        return Result.success(null);
    }
}
