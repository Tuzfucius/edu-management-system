package com.tzufucius.edu.edumanagementsystem.controller;

import com.tzufucius.edu.edumanagementsystem.common.Result;
import com.tzufucius.edu.edumanagementsystem.dto.LoginRequest;
import com.tzufucius.edu.edumanagementsystem.dto.LoginUserVO;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @PostMapping("/login")
    public Result<LoginUserVO> login(@RequestBody LoginRequest request, HttpSession session) {
        if (request.getUsername() == null || request.getPassword() == null || request.getRole() == null) {
            return Result.error("用户名、密码和角色不能为空");
        }

        if (!"123456".equals(request.getPassword())) {
            return Result.error("密码错误");
        }

        LoginUserVO user;

        switch (request.getRole()) {
            case "ADMIN":
                user = new LoginUserVO(1L, request.getUsername(), "系统管理员", "ADMIN");
                break;
            case "TEACHER":
                user = new LoginUserVO(2L, request.getUsername(), "张老师", "TEACHER");
                break;
            case "STUDENT":
                user = new LoginUserVO(3L, request.getUsername(), "李同学", "STUDENT");
                break;
            default:
                return Result.error("角色不合法");
        }

        session.setAttribute("loginUser", user);
        return Result.success(user);
    }

    @GetMapping("/me")
    public Result<LoginUserVO> me(HttpSession session) {
        LoginUserVO user = (LoginUserVO) session.getAttribute("loginUser");

        if (user == null) {
            return Result.unauthorized("未登录");
        }

        return Result.success(user);
    }

    @PostMapping("/logout")
    public Result<Void> logout(HttpSession session) {
        session.invalidate();
        return Result.success(null);
    }
}