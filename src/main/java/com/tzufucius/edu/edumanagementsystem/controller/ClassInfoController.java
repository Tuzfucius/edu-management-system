package com.tzufucius.edu.edumanagementsystem.controller;

import com.tzufucius.edu.edumanagementsystem.common.Result;
import com.tzufucius.edu.edumanagementsystem.dto.request.ClassInfoRequest;
import com.tzufucius.edu.edumanagementsystem.dto.vo.ClassInfoVO;
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
    public Result<List<ClassInfoVO>> list() {
        return Result.success(classInfoService.findAllVO());
    }

    @GetMapping("/{id}")
    public Result<ClassInfoVO> get(@PathVariable Long id) {
        return Result.success(classInfoService.findByIdVO(id));
    }

    @PostMapping
    public Result<Void> create(@RequestBody ClassInfoRequest request) {
        classInfoService.addClassInfo(request);
        return Result.success(null);
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody ClassInfoRequest request) {
        classInfoService.updateClassInfo(id, request);
        return Result.success(null);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        classInfoService.deleteClassInfo(id);
        return Result.success(null);
    }
}
