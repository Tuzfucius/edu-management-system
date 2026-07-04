package com.tzufucius.edu.edumanagementsystem.controller;

import com.tzufucius.edu.edumanagementsystem.common.Result;
import com.tzufucius.edu.edumanagementsystem.service.AcademicBusinessService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class SysUserController {
    private final AcademicBusinessService service;

    public SysUserController(AcademicBusinessService service) {
        this.service = service;
    }

    @GetMapping
    public Result<List<Map<String, Object>>> list() {
        return Result.success(service.listUsers());
    }

    @GetMapping("/{id}")
    public Result<Map<String, Object>> get(@PathVariable Long id) {
        return Result.success(service.getUser(id));
    }

    @PostMapping
    public Result<Void> create(@RequestBody Map<String, Object> body) {
        service.createUser(body);
        return Result.success(null);
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        service.updateUser(id, body);
        return Result.success(null);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        service.deleteUser(id);
        return Result.success(null);
    }
}
