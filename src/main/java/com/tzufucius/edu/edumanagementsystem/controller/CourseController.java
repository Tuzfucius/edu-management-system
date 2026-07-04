package com.tzufucius.edu.edumanagementsystem.controller;

import com.tzufucius.edu.edumanagementsystem.common.Result;
import com.tzufucius.edu.edumanagementsystem.entity.Course;
import com.tzufucius.edu.edumanagementsystem.service.CourseService;
import com.tzufucius.edu.edumanagementsystem.service.OperationLogService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService courseService;
    private final OperationLogService operationLogService;

    public CourseController(CourseService courseService, OperationLogService operationLogService) {
        this.courseService = courseService;
        this.operationLogService = operationLogService;
    }

    @GetMapping
    public Result<List<Course>> list() {
        return Result.success(courseService.findAll());
    }

    @GetMapping("/{id}")
    public Result<Course> get(@PathVariable Long id) {
        return Result.success(courseService.findById(id));
    }

    @PostMapping
    public Result<Void> create(@RequestBody Course course, HttpSession session, HttpServletRequest request) {
        courseService.addCourse(course);
        operationLogService.record(request, session, "课程管理", "INSERT", "course", null, "新增课程：" + course.getCourseName());
        return Result.success(null);
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody Course course, HttpSession session, HttpServletRequest request) {
        course.setId(id);
        courseService.updateCourse(course);
        operationLogService.record(request, session, "课程管理", "UPDATE", "course", id, "修改课程：" + course.getCourseName());
        return Result.success(null);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id, HttpSession session, HttpServletRequest request) {
        courseService.deleteCourse(id);
        operationLogService.record(request, session, "课程管理", "DISABLE", "course", id, "停用课程");
        return Result.success(null);
    }
}
