package com.tzufucius.edu.edumanagementsystem.controller;

import com.tzufucius.edu.edumanagementsystem.common.Result;
import com.tzufucius.edu.edumanagementsystem.dto.request.AcademicRequests.TeacherRequest;
import com.tzufucius.edu.edumanagementsystem.dto.vo.AcademicVOs.TeacherVO;
import com.tzufucius.edu.edumanagementsystem.service.AcademicBusinessService;
import com.tzufucius.edu.edumanagementsystem.service.OperationLogService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teachers")
public class TeacherController {
    private final AcademicBusinessService service;
    private final OperationLogService operationLogService;

    public TeacherController(AcademicBusinessService service, OperationLogService operationLogService) {
        this.service = service;
        this.operationLogService = operationLogService;
    }

    @GetMapping
    public Result<List<TeacherVO>> list() {
        return Result.success(service.listTeachers());
    }

    @GetMapping("/{id}")
    public Result<TeacherVO> get(@PathVariable Long id) {
        return Result.success(service.getTeacher(id));
    }

    @GetMapping("/by-user/{userId}")
    public Result<TeacherVO> getByUser(@PathVariable Long userId) {
        return Result.success(service.getTeacherByUserId(userId));
    }

    @PostMapping
    public Result<Void> create(@Valid @RequestBody TeacherRequest body, HttpSession session, HttpServletRequest request) {
        service.createTeacher(body);
        operationLogService.record(request, session, "教师管理", "INSERT", "teacher", null, "新增教师：" + body.teacherName());
        return Result.success(null);
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody TeacherRequest body, HttpSession session,
                               HttpServletRequest request) {
        service.updateTeacher(id, body);
        operationLogService.record(request, session, "教师管理", "UPDATE", "teacher", id, "修改教师：" + body.teacherName());
        return Result.success(null);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id, HttpSession session, HttpServletRequest request) {
        service.deleteTeacher(id);
        operationLogService.record(request, session, "教师管理", "DISABLE", "teacher", id, "停用教师");
        return Result.success(null);
    }
}
