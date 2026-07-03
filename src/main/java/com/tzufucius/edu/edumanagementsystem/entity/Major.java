package com.tzufucius.edu.edumanagementsystem.entity;

import java.time.LocalDateTime;

public class Major {

    private Long id;
    private Long collegeId;
    private String majorCode;
    private String majorName;
    private Integer schoolingYears;
    private String degreeType;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Long getId() {
        return id;
    }

    public Long getCollegeId() {
        return collegeId;
    }

    public String getMajorCode() {
        return majorCode;
    }

    public String getMajorName() {
        return majorName;
    }

    public Integer getSchoolingYears() {
        return schoolingYears;
    }

    public String getDegreeType() {
        return degreeType;
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

    public void setCollegeId(Long collegeId) {
        this.collegeId = collegeId;
    }

    public void setMajorCode(String majorCode) {
        this.majorCode = majorCode;
    }

    public void setMajorName(String majorName) {
        this.majorName = majorName;
    }

    public void setSchoolingYears(Integer schoolingYears) {
        this.schoolingYears = schoolingYears;
    }

    public void setDegreeType(String degreeType) {
        this.degreeType = degreeType;
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
