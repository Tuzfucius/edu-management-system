package com.tzufucius.edu.edumanagementsystem;

import com.tzufucius.edu.edumanagementsystem.dto.vo.LoginUserVO;
import org.springframework.mock.web.MockHttpSession;

final class TestAuth {
    private TestAuth() {
    }

    static MockHttpSession adminSession() {
        return session(1L, "admin", "管理员", "ADMIN");
    }

    static MockHttpSession teacherSession(Long userId) {
        return session(userId, "teacher-" + userId, "教师", "TEACHER");
    }

    static MockHttpSession studentSession(Long userId) {
        return session(userId, "student-" + userId, "学生", "STUDENT");
    }

    static MockHttpSession session(Long userId, String username, String displayName, String role) {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("loginUser", new LoginUserVO(userId, username, displayName, role));
        return session;
    }
}
