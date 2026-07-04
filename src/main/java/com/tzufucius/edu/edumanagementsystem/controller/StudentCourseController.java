package com.tzufucius.edu.edumanagementsystem.controller;

import com.tzufucius.edu.edumanagementsystem.common.Result;
import com.tzufucius.edu.edumanagementsystem.service.AcademicBusinessService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/student-courses")
public class StudentCourseController {
    private final AcademicBusinessService service;

    public StudentCourseController(AcademicBusinessService service) {
        this.service = service;
    }

    @GetMapping
    public Result<List<Map<String, Object>>> list(
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) Long teacherId
    ) {
        return Result.success(service.listStudentCourses(studentId, teacherId));
    }

    @GetMapping("/selectable")
    public Result<List<Map<String, Object>>> selectable(@RequestParam Long studentId, @RequestParam String semester) {
        return Result.success(service.listSelectableTasks(studentId, semester));
    }

    @PostMapping
    public Result<Void> select(@RequestBody Map<String, Object> body) {
        service.selectCourse(body);
        return Result.success(null);
    }

    @DeleteMapping("/{id}")
    public Result<Void> drop(@PathVariable Long id) {
        service.dropCourse(id);
        return Result.success(null);
    }

    @PutMapping("/{id}/score")
    public Result<Void> updateScore(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        service.updateScore(id, body);
        return Result.success(null);
    }
}
