package com.tzufucius.edu.edumanagementsystem.controller;

import com.tzufucius.edu.edumanagementsystem.common.Result;
import com.tzufucius.edu.edumanagementsystem.dto.request.BasicRequests.DepartmentRequest;
import com.tzufucius.edu.edumanagementsystem.dto.vo.BasicVOs.DepartmentVO;
import com.tzufucius.edu.edumanagementsystem.service.DepartmentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/departments")
public class DepartmentController {
    private final DepartmentService departmentService;

    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @GetMapping
    public Result<List<DepartmentVO>> list() {
        return Result.success(departmentService.findAllVO());
    }

    @GetMapping("/{id}")
    public Result<DepartmentVO> get(@PathVariable Long id) {
        return Result.success(departmentService.findByIdVO(id));
    }

    @PostMapping
    public Result<Void> create(@RequestBody DepartmentRequest request) {
        departmentService.addDepartment(request);
        return Result.success(null);
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody DepartmentRequest request) {
        departmentService.updateDepartment(id, request);
        return Result.success(null);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        departmentService.deleteDepartment(id);
        return Result.success(null);
    }
}
