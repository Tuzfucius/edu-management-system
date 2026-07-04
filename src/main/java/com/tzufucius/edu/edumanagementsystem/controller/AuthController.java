package com.tzufucius.edu.edumanagementsystem.controller;

import com.tzufucius.edu.edumanagementsystem.common.Result;
import com.tzufucius.edu.edumanagementsystem.dto.LoginRequest;
import com.tzufucius.edu.edumanagementsystem.dto.LoginUserVO;
import com.tzufucius.edu.edumanagementsystem.service.AcademicBusinessService;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AcademicBusinessService academicBusinessService;

    public AuthController(AcademicBusinessService academicBusinessService) {
        this.academicBusinessService = academicBusinessService;
    }

    @PostMapping("/login")
    public Result<LoginUserVO> login(@RequestBody LoginRequest request, HttpSession session) {
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
