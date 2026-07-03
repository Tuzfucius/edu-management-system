package com.tzufucius.edu.edumanagementsystem.entity;

import java.time.LocalDateTime;

public class ClassInfo {

    private Long id;
    private Long majorId;
    private String classCode;
    private String className;
    private Integer entranceYear;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Long getId() {
        return id;
    }

    public Long getMajorId() {
        return majorId;
    }

    public String getClassCode() {
        return classCode;
    }

    public String getClassName() {
        return className;
    }

    public Integer getEntranceYear() {
        return entranceYear;
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

    public void setMajorId(Long majorId) {
        this.majorId = majorId;
    }

    public void setClassCode(String classCode) {
        this.classCode = classCode;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public void setEntranceYear(Integer entranceYear) {
        this.entranceYear = entranceYear;
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
