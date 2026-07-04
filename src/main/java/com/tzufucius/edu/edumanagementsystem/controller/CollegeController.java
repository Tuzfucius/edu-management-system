package com.tzufucius.edu.edumanagementsystem.controller;

import com.tzufucius.edu.edumanagementsystem.common.Result;
import com.tzufucius.edu.edumanagementsystem.entity.College;
import com.tzufucius.edu.edumanagementsystem.service.CollegeService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/colleges")
public class CollegeController {

    private final CollegeService collegeService;

    public CollegeController(CollegeService collegeService) {
        this.collegeService = collegeService;
    }

    @GetMapping
    public Result<List<College>> list() {
        return Result.success(collegeService.findAll());
    }

    @GetMapping("/{id}")
    public Result<College> get(@PathVariable Long id) {
        return Result.success(collegeService.findById(id));
    }

    @PostMapping
    public Result<Void> create(@RequestBody College college) {
        collegeService.addCollege(college);
        return Result.success(null);
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody College college) {
        college.setId(id);
        collegeService.updateCollege(college);
        return Result.success(null);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        collegeService.deleteCollege(id);
        return Result.success(null);
    }
}
