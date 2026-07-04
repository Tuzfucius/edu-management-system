package com.tzufucius.edu.edumanagementsystem.service;

import com.tzufucius.edu.edumanagementsystem.common.PageResult;
import com.tzufucius.edu.edumanagementsystem.dao.OperationLogDao;
import com.tzufucius.edu.edumanagementsystem.dto.LoginUserVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class OperationLogService {
    private static final Logger log = LoggerFactory.getLogger(OperationLogService.class);

    private final OperationLogDao operationLogDao;

    public OperationLogService(OperationLogDao operationLogDao) {
        this.operationLogDao = operationLogDao;
    }

    public void record(HttpServletRequest request, HttpSession session, String moduleName, String operationType,
                       String targetTable, Long targetId, String description) {
        Long userId = currentUserId(session);
        record(request, userId, moduleName, operationType, targetTable, targetId, description);
    }

    public void record(HttpServletRequest request, Long userId, String moduleName, String operationType,
                       String targetTable, Long targetId, String description) {
        try {
            operationLogDao.insert(userId, moduleName, operationType, targetTable, targetId, description, clientIp(request));
        } catch (DataAccessException exception) {
            log.warn("写入操作日志失败 module={} operation={} targetTable={} targetId={}",
                    moduleName, operationType, targetTable, targetId, exception);
        }
    }

    public PageResult<Map<String, Object>> page(String moduleName, String operationType, Long userId,
                                                String startTime, String endTime, Integer page, Integer size) {
        int safePage = page == null || page < 1 ? 1 : page;
        int safeSize = size == null || size < 1 ? 10 : Math.min(size, 100);
        return operationLogDao.page(moduleName, operationType, userId, startTime, endTime, safePage, safeSize);
    }

    private Long currentUserId(HttpSession session) {
        if (session == null) {
            return null;
        }
        Object loginUser = session.getAttribute("loginUser");
        if (loginUser instanceof LoginUserVO user) {
            return user.getId();
        }
        return null;
    }

    private String clientIp(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }
        String realIp = request.getHeader("X-Real-IP");
        if (realIp != null && !realIp.isBlank()) {
            return realIp;
        }
        return request.getRemoteAddr();
    }
}

