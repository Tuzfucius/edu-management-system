package com.tzufucius.edu.edumanagementsystem.controller;

import com.tzufucius.edu.edumanagementsystem.auth.AuthContext;
import com.tzufucius.edu.edumanagementsystem.auth.RequireRole;
import com.tzufucius.edu.edumanagementsystem.common.Result;
import com.tzufucius.edu.edumanagementsystem.dto.request.TeachingTaskRequest;
import com.tzufucius.edu.edumanagementsystem.dto.vo.TeachingTaskVO;
import com.tzufucius.edu.edumanagementsystem.service.AcademicBusinessService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teaching-tasks")
@RequireRole("ADMIN")
public class TeachingTaskController {
    private final AcademicBusinessService service;

    public TeachingTaskController(AcademicBusinessService service) {
        this.service = service;
    }

    @GetMapping
    @RequireRole({"ADMIN", "TEACHER"})
    public Result<List<TeachingTaskVO>> list(@RequestParam(required = false) Long teacherId, HttpSession session) {
        return Result.success(service.listTeachingTasks(teacherId, AuthContext.requireCurrentUser(session)));
    }

    @GetMapping("/{id}")
    @RequireRole({"ADMIN", "TEACHER"})
    public Result<TeachingTaskVO> get(@PathVariable Long id, HttpSession session) {
        return Result.success(service.getTeachingTask(id, AuthContext.requireCurrentUser(session)));
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
