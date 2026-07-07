package com.tzufucius.edu.edumanagementsystem.controller;

import com.tzufucius.edu.edumanagementsystem.common.Result;
import com.tzufucius.edu.edumanagementsystem.auth.RequireRole;
import com.tzufucius.edu.edumanagementsystem.dto.request.SysUserRequest;
import com.tzufucius.edu.edumanagementsystem.dto.vo.SysUserVO;
import com.tzufucius.edu.edumanagementsystem.service.AcademicBusinessService;
import com.tzufucius.edu.edumanagementsystem.service.OperationLogService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequireRole("ADMIN")
public class SysUserController {
    private final AcademicBusinessService service;
    private final OperationLogService operationLogService;

    public SysUserController(AcademicBusinessService service, OperationLogService operationLogService) {
        this.service = service;
        this.operationLogService = operationLogService;
    }

    @GetMapping
    public Result<List<SysUserVO>> list() {
        return Result.success(service.listUsers());
    }

    @GetMapping("/{id}")
    public Result<SysUserVO> get(@PathVariable Long id) {
        return Result.success(service.getUser(id));
    }

    @PostMapping
    public Result<Long> create(@Valid @RequestBody SysUserRequest body, HttpSession session, HttpServletRequest request) {
        Long userId = service.createUser(body);
        operationLogService.record(request, session, "账号管理", "INSERT", "sys_user", null, "新增账号：" + body.username());
        return Result.success(userId);
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody SysUserRequest body, HttpSession session,
                               HttpServletRequest request) {
        service.updateUser(id, body);
        operationLogService.record(request, session, "账号管理", "UPDATE", "sys_user", id, "修改账号：" + body.username());
        return Result.success(null);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id, HttpSession session, HttpServletRequest request) {
        service.deleteUser(id);
        operationLogService.record(request, session, "账号管理", "DISABLE", "sys_user", id, "停用账号");
        return Result.success(null);
    }
}
