package com.tzufucius.edu.edumanagementsystem.controller;

import com.tzufucius.edu.edumanagementsystem.common.Result;
import com.tzufucius.edu.edumanagementsystem.service.AcademicBusinessService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/students")
public class StudentController {
    private final AcademicBusinessService service;

    public StudentController(AcademicBusinessService service) {
        this.service = service;
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
    public Result<Void> create(@RequestBody Map<String, Object> body) {
        service.createStudent(body);
        return Result.success(null);
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        service.updateStudent(id, body);
        return Result.success(null);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        service.deleteStudent(id);
        return Result.success(null);
    }
}
