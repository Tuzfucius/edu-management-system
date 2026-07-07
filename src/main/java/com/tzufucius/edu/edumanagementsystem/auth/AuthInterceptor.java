package com.tzufucius.edu.edumanagementsystem.auth;

import com.tzufucius.edu.edumanagementsystem.common.Result;
import com.tzufucius.edu.edumanagementsystem.dto.vo.LoginUserVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

@Component
public class AuthInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod()) || isPublicEndpoint(request)) {
            return true;
        }
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        HttpSession session = request.getSession(false);
        LoginUserVO user = AuthContext.getCurrentUser(session);
        if (user == null) {
            writeResult(response, HttpServletResponse.SC_UNAUTHORIZED, Result.unauthorized("未登录"));
            return false;
        }

        RequireRole requireRole = findRequireRole(handlerMethod);
        if (requireRole != null && !AuthContext.hasAnyRole(user, requireRole.value())) {
            writeResult(response, HttpServletResponse.SC_FORBIDDEN, Result.forbidden("无权访问"));
            return false;
        }
        return true;
    }

    private boolean isPublicEndpoint(HttpServletRequest request) {
        return "/api/auth/login".equals(request.getServletPath());
    }

    private RequireRole findRequireRole(HandlerMethod handlerMethod) {
        RequireRole methodRole = handlerMethod.getMethodAnnotation(RequireRole.class);
        if (methodRole != null) {
            return methodRole;
        }
        return handlerMethod.getBeanType().getAnnotation(RequireRole.class);
    }

    private void writeResult(HttpServletResponse response, int status, Result<Void> result) throws IOException {
        response.setStatus(status);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("""
                {"code":%d,"message":"%s","data":null}
                """.formatted(result.getCode(), escapeJson(result.getMessage())).trim());
    }

    private String escapeJson(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
