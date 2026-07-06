package com.tzufucius.edu.edumanagementsystem.controller;

import com.tzufucius.edu.edumanagementsystem.common.Result;
import com.tzufucius.edu.edumanagementsystem.dto.request.AcademicRequests.TeacherStudentRequest;
import com.tzufucius.edu.edumanagementsystem.dto.vo.AcademicVOs.TeacherStudentVO;
import com.tzufucius.edu.edumanagementsystem.service.AcademicBusinessService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teacher-students")
public class TeacherStudentController {
    private final AcademicBusinessService service;

    public TeacherStudentController(AcademicBusinessService service) {
        this.service = service;
    }

    @GetMapping
    public Result<List<TeacherStudentVO>> list() {
        return Result.success(service.listTeacherStudents());
    }

    @PostMapping
    public Result<Void> create(@Valid @RequestBody TeacherStudentRequest body) {
        service.createTeacherStudent(body);
        return Result.success(null);
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody TeacherStudentRequest body) {
        service.updateTeacherStudent(id, body);
        return Result.success(null);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        service.deleteTeacherStudent(id);
        return Result.success(null);
    }
}
