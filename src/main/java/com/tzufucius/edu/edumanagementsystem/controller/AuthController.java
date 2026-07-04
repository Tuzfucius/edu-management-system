package com.tzufucius.edu.edumanagementsystem.controller;

import com.tzufucius.edu.edumanagementsystem.common.Result;
import com.tzufucius.edu.edumanagementsystem.dto.LoginRequest;
import com.tzufucius.edu.edumanagementsystem.dto.LoginUserVO;
import com.tzufucius.edu.edumanagementsystem.service.AcademicBusinessService;
import com.tzufucius.edu.edumanagementsystem.service.OperationLogService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AcademicBusinessService academicBusinessService;
    private final OperationLogService operationLogService;

    public AuthController(AcademicBusinessService academicBusinessService, OperationLogService operationLogService) {
        this.academicBusinessService = academicBusinessService;
        this.operationLogService = operationLogService;
    }

    @PostMapping("/login")
    public Result<LoginUserVO> login(@RequestBody LoginRequest request, HttpSession session, HttpServletRequest httpRequest) {
        Map<String, Object> loginUser = academicBusinessService.login(
                request.getUsername(),
                request.getPassword(),
                request.getRole()
        );
        LoginUserVO user = new LoginUserVO(
                Long.parseLong(loginUser.get("id").toString()),
                loginUser.get("username").toString(),
                loginUser.get("displayName").toString(),
                loginUser.get("role").toString()
        );
        session.setAttribute("loginUser", user);
        operationLogService.record(httpRequest, user.getId(), "登录认证", "LOGIN", "sys_user", user.getId(), "用户登录：" + user.getUsername());
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
    public Result<Void> logout(HttpSession session, HttpServletRequest request) {
        LoginUserVO user = (LoginUserVO) session.getAttribute("loginUser");
        Long userId = user == null ? null : user.getId();
        operationLogService.record(request, userId, "登录认证", "LOGOUT", "sys_user", userId, "用户退出登录");
        session.invalidate();
        return Result.success(null);
    }
}
