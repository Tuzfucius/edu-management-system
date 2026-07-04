package com.tzufucius.edu.edumanagementsystem.controller;

import com.tzufucius.edu.edumanagementsystem.common.Result;
import com.tzufucius.edu.edumanagementsystem.entity.Major;
import com.tzufucius.edu.edumanagementsystem.service.MajorService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/majors")
public class MajorController {

    private final MajorService majorService;

    public MajorController(MajorService majorService) {
        this.majorService = majorService;
    }

    @GetMapping
    public Result<List<Major>> list() {
        return Result.success(majorService.findAll());
    }

    @GetMapping("/{id}")
    public Result<Major> get(@PathVariable Long id) {
        return Result.success(majorService.findById(id));
    }

    @PostMapping
    public Result<Void> create(@RequestBody Major major) {
        majorService.addMajor(major);
        return Result.success(null);
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody Major major) {
        major.setId(id);
        majorService.updateMajor(major);
        return Result.success(null);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        majorService.deleteMajor(id);
        return Result.success(null);
    }
}
