package com.tzufucius.edu.edumanagementsystem.controller;

import com.tzufucius.edu.edumanagementsystem.common.Result;
import com.tzufucius.edu.edumanagementsystem.service.AcademicBusinessService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/teaching-tasks")
public class TeachingTaskController {
    private final AcademicBusinessService service;

    public TeachingTaskController(AcademicBusinessService service) {
        this.service = service;
    }

    @GetMapping
    public Result<List<Map<String, Object>>> list(@RequestParam(required = false) Long teacherId) {
        return Result.success(service.listTeachingTasks(teacherId));
    }

    @GetMapping("/{id}")
    public Result<Map<String, Object>> get(@PathVariable Long id) {
        return Result.success(service.getTeachingTask(id));
    }

    @PostMapping
    public Result<Void> create(@RequestBody Map<String, Object> body) {
        service.createTeachingTask(body);
        return Result.success(null);
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        service.updateTeachingTask(id, body);
        return Result.success(null);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        service.deleteTeachingTask(id);
        return Result.success(null);
    }
}
