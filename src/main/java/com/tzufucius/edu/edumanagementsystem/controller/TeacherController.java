package com.tzufucius.edu.edumanagementsystem.controller;

import com.tzufucius.edu.edumanagementsystem.common.Result;
import com.tzufucius.edu.edumanagementsystem.service.AcademicBusinessService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/teachers")
public class TeacherController {
    private final AcademicBusinessService service;

    public TeacherController(AcademicBusinessService service) {
        this.service = service;
    }

    @GetMapping
    public Result<List<Map<String, Object>>> list() {
        return Result.success(service.listTeachers());
    }

    @GetMapping("/{id}")
    public Result<Map<String, Object>> get(@PathVariable Long id) {
        return Result.success(service.getTeacher(id));
    }

    @GetMapping("/by-user/{userId}")
    public Result<Map<String, Object>> getByUser(@PathVariable Long userId) {
        return Result.success(service.getTeacherByUserId(userId));
    }

    @PostMapping
    public Result<Void> create(@RequestBody Map<String, Object> body) {
        service.createTeacher(body);
        return Result.success(null);
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        service.updateTeacher(id, body);
        return Result.success(null);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        service.deleteTeacher(id);
        return Result.success(null);
    }
}
