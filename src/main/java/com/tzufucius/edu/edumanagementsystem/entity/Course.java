package com.tzufucius.edu.edumanagementsystem.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Course {

    private Long id;
    private String courseCode;
    private String courseName;
    private BigDecimal credit;
    private Integer totalHours;
    private String courseType;
    private String examType;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Long getId() {
        return id;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public String getCourseName() {
        return courseName;
    }

    public BigDecimal getCredit() {
        return credit;
    }

    public Integer getTotalHours() {
        return totalHours;
    }

    public String getCourseType() {
        return courseType;
    }

    public String getExamType() {
        return examType;
    }

    public Integer getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public void setCredit(BigDecimal credit) {
        this.credit = credit;
    }

    public void setTotalHours(Integer totalHours) {
        this.totalHours = totalHours;
    }

    public void setCourseType(String courseType) {
        this.courseType = courseType;
    }

    public void setExamType(String examType) {
        this.examType = examType;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
