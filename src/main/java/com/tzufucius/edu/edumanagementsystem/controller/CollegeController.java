package com.tzufucius.edu.edumanagementsystem.controller;

import com.tzufucius.edu.edumanagementsystem.common.Result;
import com.tzufucius.edu.edumanagementsystem.dto.request.CollegeRequest;
import com.tzufucius.edu.edumanagementsystem.dto.vo.CollegeVO;
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
    public Result<List<CollegeVO>> list() {
        return Result.success(collegeService.findAllVO());
    }

    @GetMapping("/{id}")
    public Result<CollegeVO> get(@PathVariable Long id) {
        return Result.success(collegeService.findByIdVO(id));
    }

    @PostMapping
    public Result<Void> create(@RequestBody CollegeRequest request) {
        collegeService.addCollege(request);
        return Result.success(null);
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody CollegeRequest request) {
        collegeService.updateCollege(id, request);
        return Result.success(null);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        collegeService.deleteCollege(id);
        return Result.success(null);
    }
}
