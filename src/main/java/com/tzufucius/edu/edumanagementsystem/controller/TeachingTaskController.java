package com.tzufucius.edu.edumanagementsystem.controller;

import com.tzufucius.edu.edumanagementsystem.common.Result;
import com.tzufucius.edu.edumanagementsystem.dto.request.AcademicRequests.TeachingTaskRequest;
import com.tzufucius.edu.edumanagementsystem.dto.vo.AcademicVOs.TeachingTaskVO;
import com.tzufucius.edu.edumanagementsystem.service.AcademicBusinessService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teaching-tasks")
public class TeachingTaskController {
    private final AcademicBusinessService service;

    public TeachingTaskController(AcademicBusinessService service) {
        this.service = service;
    }

    @GetMapping
    public Result<List<TeachingTaskVO>> list(@RequestParam(required = false) Long teacherId) {
        return Result.success(service.listTeachingTasks(teacherId));
    }

    @GetMapping("/{id}")
    public Result<TeachingTaskVO> get(@PathVariable Long id) {
        return Result.success(service.getTeachingTask(id));
    }

    @PostMapping
    public Result<Void> create(@Valid @RequestBody TeachingTaskRequest body) {
        service.createTeachingTask(body);
        return Result.success(null);
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody TeachingTaskRequest body) {
        service.updateTeachingTask(id, body);
        return Result.success(null);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        service.deleteTeachingTask(id);
        return Result.success(null);
    }
}
