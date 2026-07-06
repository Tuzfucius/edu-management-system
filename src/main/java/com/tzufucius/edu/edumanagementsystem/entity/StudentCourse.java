package com.tzufucius.edu.edumanagementsystem.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class StudentCourse {
    private Long id;
    private Long studentId;
    private Long teachingTaskId;
    private LocalDateTime selectTime;
    private BigDecimal score;
    private Integer gradeStatus;
    private Integer status;
    private String remark;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }
    public Long getTeachingTaskId() { return teachingTaskId; }
    public void setTeachingTaskId(Long teachingTaskId) { this.teachingTaskId = teachingTaskId; }
    public LocalDateTime getSelectTime() { return selectTime; }
    public void setSelectTime(LocalDateTime selectTime) { this.selectTime = selectTime; }
    public BigDecimal getScore() { return score; }
    public void setScore(BigDecimal score) { this.score = score; }
    public Integer getGradeStatus() { return gradeStatus; }
    public void setGradeStatus(Integer gradeStatus) { this.gradeStatus = gradeStatus; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
