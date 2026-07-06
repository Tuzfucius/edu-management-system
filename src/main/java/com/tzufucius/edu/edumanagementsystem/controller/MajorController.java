package com.tzufucius.edu.edumanagementsystem.controller;

import com.tzufucius.edu.edumanagementsystem.common.Result;
import com.tzufucius.edu.edumanagementsystem.dto.request.BasicRequests.MajorRequest;
import com.tzufucius.edu.edumanagementsystem.dto.vo.BasicVOs.MajorVO;
import com.tzufucius.edu.edumanagementsystem.service.MajorService;
import jakarta.validation.Valid;
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
    public Result<List<MajorVO>> list() {
        return Result.success(majorService.findAll());
    }

    @GetMapping("/{id}")
    public Result<MajorVO> get(@PathVariable Long id) {
        return Result.success(majorService.findById(id));
    }

    @PostMapping
    public Result<Void> create(@Valid @RequestBody MajorRequest request) {
        majorService.addMajor(request);
        return Result.success(null);
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody MajorRequest request) {
        majorService.updateMajor(id, request);
        return Result.success(null);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        majorService.deleteMajor(id);
        return Result.success(null);
    }
}
