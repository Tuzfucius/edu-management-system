package com.tzufucius.edu.edumanagementsystem.mapper;

import com.tzufucius.edu.edumanagementsystem.dto.request.StudentRequest;
import com.tzufucius.edu.edumanagementsystem.dto.request.SysUserRequest;
import com.tzufucius.edu.edumanagementsystem.dto.request.TeacherRequest;
import com.tzufucius.edu.edumanagementsystem.dto.request.TeacherStudentRequest;
import com.tzufucius.edu.edumanagementsystem.dto.request.TeachingTaskRequest;
import com.tzufucius.edu.edumanagementsystem.dto.vo.OperationLogVO;
import com.tzufucius.edu.edumanagementsystem.dto.vo.SelectableTaskVO;
import com.tzufucius.edu.edumanagementsystem.dto.vo.StudentCourseVO;
import com.tzufucius.edu.edumanagementsystem.dto.vo.StudentVO;
import com.tzufucius.edu.edumanagementsystem.dto.vo.SysUserVO;
import com.tzufucius.edu.edumanagementsystem.dto.vo.TeacherStudentVO;
import com.tzufucius.edu.edumanagementsystem.dto.vo.TeacherVO;
import com.tzufucius.edu.edumanagementsystem.dto.vo.TeachingTaskVO;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public final class AcademicMapper {
    private AcademicMapper() {
    }

    public static Map<String, Object> toMap(SysUserRequest request) {
        Map<String, Object> map = new HashMap<>();
        map.put("username", request.username());
        map.put("password", request.password());
        map.put("role", request.role());
        map.put("status", request.status());
        return map;
    }

    public static Map<String, Object> toMap(StudentRequest request) {
        Map<String, Object> map = new HashMap<>();
        map.put("userId", request.userId());
        map.put("account", request.account() == null ? null : toMap(request.account()));
        map.put("classId", request.classId());
        map.put("studentNo", request.studentNo());
        map.put("studentName", request.studentName());
        map.put("gender", request.gender());
        map.put("birthDate", request.birthDate());
        map.put("phone", request.phone());
        map.put("email", request.email());
        map.put("enrollmentYear", request.enrollmentYear());
        return map;
    }

    public static Map<String, Object> toMap(TeacherRequest request) {
        Map<String, Object> map = new HashMap<>();
        map.put("userId", request.userId());
        map.put("account", request.account() == null ? null : toMap(request.account()));
        map.put("departmentId", request.departmentId());
        map.put("teacherNo", request.teacherNo());
        map.put("teacherName", request.teacherName());
        map.put("gender", request.gender());
        map.put("title", request.title());
        map.put("phone", request.phone());
        map.put("email", request.email());
        return map;
    }

    public static Map<String, Object> toMap(TeachingTaskRequest request) {
        Map<String, Object> map = new HashMap<>();
        map.put("courseId", request.courseId());
        map.put("teacherId", request.teacherId());
        map.put("semester", request.semester());
        map.put("weekday", request.weekday());
        map.put("startSection", request.startSection());
        map.put("endSection", request.endSection());
        map.put("weeks", request.weeks());
        map.put("classroom", request.classroom());
        map.put("capacity", request.capacity());
        map.put("taskStatus", request.taskStatus());
        return map;
    }

    public static Map<String, Object> toMap(TeacherStudentRequest request) {
        Map<String, Object> map = new HashMap<>();
        map.put("teacherId", request.teacherId());
        map.put("studentId", request.studentId());
        map.put("guideType", request.guideType());
        map.put("startDate", request.startDate());
        map.put("endDate", request.endDate());
        return map;
    }

    public static SysUserVO toSysUserVO(Map<String, Object> map) {
        return new SysUserVO(longValue(map.get("id")), text(map.get("username")), text(map.get("role")),
                intValue(map.get("status")), localDateTime(map.get("lastLoginAt")),
                localDateTime(map.get("createdAt")), localDateTime(map.get("updatedAt")));
    }

    public static StudentVO toStudentVO(Map<String, Object> map) {
        return new StudentVO(longValue(map.get("id")), longValue(map.get("userId")), longValue(map.get("classId")),
                text(map.get("studentNo")), text(map.get("studentName")), text(map.get("gender")),
                localDate(map.get("birthDate")), text(map.get("phone")), text(map.get("email")),
                intValue(map.get("enrollmentYear")), intValue(map.get("status")), text(map.get("className")),
                text(map.get("majorName")), text(map.get("collegeName")), text(map.get("username")));
    }

    public static TeacherVO toTeacherVO(Map<String, Object> map) {
        return new TeacherVO(longValue(map.get("id")), longValue(map.get("userId")), longValue(map.get("departmentId")),
                text(map.get("teacherNo")), text(map.get("teacherName")), text(map.get("gender")),
                text(map.get("title")), text(map.get("phone")), text(map.get("email")), intValue(map.get("status")),
                text(map.get("departmentName")), text(map.get("collegeName")), text(map.get("username")));
    }

    public static TeachingTaskVO toTeachingTaskVO(Map<String, Object> map) {
        return new TeachingTaskVO(longValue(map.get("id")), longValue(map.get("courseId")), longValue(map.get("teacherId")),
                text(map.get("semester")), intValue(map.get("weekday")), intValue(map.get("startSection")),
                intValue(map.get("endSection")), text(map.get("weeks")), text(map.get("classroom")),
                intValue(map.get("capacity")), intValue(map.get("selectedCount")), intValue(map.get("taskStatus")),
                text(map.get("courseCode")), text(map.get("courseName")), decimalValue(map.get("credit")),
                text(map.get("teacherNo")), text(map.get("teacherName")));
    }

    public static StudentCourseVO toStudentCourseVO(Map<String, Object> map) {
        return new StudentCourseVO(longValue(map.get("id")), longValue(map.get("studentId")),
                longValue(map.get("teachingTaskId")), localDateTime(map.get("selectTime")),
                decimalValue(map.get("score")), intValue(map.get("gradeStatus")), intValue(map.get("status")),
                text(map.get("remark")), text(map.get("studentNo")), text(map.get("studentName")),
                text(map.get("courseCode")), text(map.get("courseName")), decimalValue(map.get("credit")),
                text(map.get("teacherName")), text(map.get("semester")), intValue(map.get("weekday")),
                intValue(map.get("startSection")), intValue(map.get("endSection")), text(map.get("classroom")));
    }

    public static SelectableTaskVO toSelectableTaskVO(Map<String, Object> map) {
        return new SelectableTaskVO(longValue(map.get("id")), longValue(map.get("courseId")),
                longValue(map.get("teacherId")), text(map.get("semester")), intValue(map.get("weekday")),
                intValue(map.get("startSection")), intValue(map.get("endSection")), text(map.get("weeks")),
                text(map.get("classroom")), intValue(map.get("capacity")), intValue(map.get("selectedCount")),
                text(map.get("courseCode")), text(map.get("courseName")), decimalValue(map.get("credit")),
                text(map.get("teacherName")), intValue(map.get("selectedByMe")), longValue(map.get("studentCourseId")));
    }

    public static TeacherStudentVO toTeacherStudentVO(Map<String, Object> map) {
        return new TeacherStudentVO(longValue(map.get("id")), longValue(map.get("teacherId")),
                longValue(map.get("studentId")), text(map.get("guideType")), localDate(map.get("startDate")),
                localDate(map.get("endDate")), intValue(map.get("status")), text(map.get("teacherName")),
                text(map.get("studentNo")), text(map.get("studentName")));
    }

    public static OperationLogVO toOperationLogVO(Map<String, Object> map) {
        return new OperationLogVO(longValue(map.get("id")), longValue(map.get("userId")), text(map.get("username")),
                text(map.get("moduleName")), text(map.get("operationType")), text(map.get("targetTable")),
                longValue(map.get("targetId")), text(map.get("description")), text(map.get("ipAddress")),
                localDateTime(map.get("createdAt")));
    }

    private static String text(Object value) {
        return value == null ? null : value.toString();
    }

    private static Long longValue(Object value) {
        return value == null ? null : Long.parseLong(value.toString());
    }

    private static Integer intValue(Object value) {
        return value == null ? null : Integer.parseInt(value.toString());
    }

    private static BigDecimal decimalValue(Object value) {
        return value == null ? null : new BigDecimal(value.toString());
    }

    private static LocalDate localDate(Object value) {
        if (value instanceof LocalDate localDate) {
            return localDate;
        }
        if (value instanceof Date date) {
            return date.toLocalDate();
        }
        return value == null ? null : LocalDate.parse(value.toString());
    }

    private static LocalDateTime localDateTime(Object value) {
        if (value instanceof LocalDateTime localDateTime) {
            return localDateTime;
        }
        if (value instanceof Timestamp timestamp) {
            return timestamp.toLocalDateTime();
        }
        return value == null ? null : LocalDateTime.parse(value.toString().replace(' ', 'T'));
    }
}
