package com.tzufucius.edu.edumanagementsystem.auth;

import com.tzufucius.edu.edumanagementsystem.dto.vo.LoginUserVO;
import jakarta.servlet.http.HttpSession;

import java.util.Arrays;

public final class AuthContext {
    public static final String LOGIN_USER_SESSION_KEY = "loginUser";

    private AuthContext() {
    }

    public static LoginUserVO getCurrentUser(HttpSession session) {
        if (session == null) {
            return null;
        }
        Object user = session.getAttribute(LOGIN_USER_SESSION_KEY);
        return user instanceof LoginUserVO loginUser ? loginUser : null;
    }

    public static LoginUserVO requireCurrentUser(HttpSession session) {
        LoginUserVO user = getCurrentUser(session);
        if (user == null) {
            throw new AuthException(401, "未登录");
        }
        return user;
    }

    public static boolean hasAnyRole(LoginUserVO user, String... roles) {
        if (user == null || user.getRole() == null) {
            return false;
        }
        return Arrays.asList(roles).contains(user.getRole());
    }

    public static void requireAnyRole(LoginUserVO user, String... roles) {
        if (!hasAnyRole(user, roles)) {
            throw new AuthException(403, "无权访问");
        }
    }
}
