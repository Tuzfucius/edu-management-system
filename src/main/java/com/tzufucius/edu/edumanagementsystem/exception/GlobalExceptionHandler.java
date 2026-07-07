package com.tzufucius.edu.edumanagementsystem.exception;

import com.tzufucius.edu.edumanagementsystem.auth.AuthException;
import com.tzufucius.edu.edumanagementsystem.common.Result;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.dao.DataAccessException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException exception, HttpServletRequest request) {
        log.warn("业务异常 requestId={} uri={} message={}", requestId(), request.getRequestURI(), exception.getMessage());
        return new Result<>(exception.getCode(), exception.getMessage(), null);
    }

    @ExceptionHandler(AuthException.class)
    public Result<Void> handleAuthException(AuthException exception, HttpServletRequest request) {
        log.warn("权限校验失败 requestId={} uri={} code={} message={}", requestId(), request.getRequestURI(), exception.getCode(), exception.getMessage());
        return new Result<>(exception.getCode(), exception.getMessage(), null);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Result<Void> handleHttpMessageNotReadableException(HttpMessageNotReadableException exception, HttpServletRequest request) {
        log.warn("请求体解析失败 requestId={} uri={} message={}", requestId(), request.getRequestURI(), exception.getMessage());
        return Result.badRequest("请求体格式不正确");
    }

    @ExceptionHandler({
            MissingServletRequestParameterException.class,
            MethodArgumentTypeMismatchException.class,
            IllegalArgumentException.class,
            NumberFormatException.class
    })
    public Result<Void> handleBadRequest(Exception exception, HttpServletRequest request) {
        log.warn("请求参数不合法 requestId={} uri={} message={}", requestId(), request.getRequestURI(), exception.getMessage());
        return Result.badRequest("请求参数不正确");
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public Result<Void> handleValidationException(Exception exception, HttpServletRequest request) {
        log.warn("参数校验失败 requestId={} uri={} message={}", requestId(), request.getRequestURI(), exception.getMessage());
        return Result.error("请求参数校验失败");
    }

    @ExceptionHandler(DataAccessException.class)
    public Result<Void> handleDataAccessException(DataAccessException exception, HttpServletRequest request) {
        log.error("数据库访问异常 requestId={} uri={}", requestId(), request.getRequestURI(), exception);
        return Result.serverError("数据库操作失败，请稍后重试");
    }

    @ExceptionHandler(RuntimeException.class)
    public Result<Void> handleRuntimeException(RuntimeException exception, HttpServletRequest request) {
        log.warn("运行时异常 requestId={} uri={} message={}", requestId(), request.getRequestURI(), exception.getMessage());
        return Result.error(exception.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception exception, HttpServletRequest request) {
        log.error("未知系统异常 requestId={} uri={}", requestId(), request.getRequestURI(), exception);
        return Result.serverError("系统异常，请联系管理员，requestId=" + requestId());
    }

    private String requestId() {
        String requestId = MDC.get("requestId");
        return requestId == null ? "-" : requestId;
    }
}
