package com.tzufucius.edu.edumanagementsystem.controller;

import com.tzufucius.edu.edumanagementsystem.common.Result;
import com.tzufucius.edu.edumanagementsystem.entity.ClassInfo;
import com.tzufucius.edu.edumanagementsystem.service.ClassInfoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/classes")
public class ClassInfoController {

    private final ClassInfoService classInfoService;

    public ClassInfoController(ClassInfoService classInfoService) {
        this.classInfoService = classInfoService;
    }

    @GetMapping
    public Result<List<ClassInfo>> list() {
        return Result.success(classInfoService.findAll());
    }

    @GetMapping("/{id}")
    public Result<ClassInfo> get(@PathVariable Long id) {
        return Result.success(classInfoService.findById(id));
    }

    @PostMapping
    public Result<Void> create(@RequestBody ClassInfo classInfo) {
        classInfoService.addClassInfo(classInfo);
        return Result.success(null);
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody ClassInfo classInfo) {
        classInfo.setId(id);
        classInfoService.updateClassInfo(classInfo);
        return Result.success(null);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        classInfoService.deleteClassInfo(id);
        return Result.success(null);
    }
}
