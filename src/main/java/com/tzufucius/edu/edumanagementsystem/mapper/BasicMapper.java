package com.tzufucius.edu.edumanagementsystem.mapper;

import com.tzufucius.edu.edumanagementsystem.dto.request.BasicRequests.ClassInfoRequest;
import com.tzufucius.edu.edumanagementsystem.dto.request.BasicRequests.CollegeRequest;
import com.tzufucius.edu.edumanagementsystem.dto.request.BasicRequests.CourseRequest;
import com.tzufucius.edu.edumanagementsystem.dto.request.BasicRequests.DepartmentRequest;
import com.tzufucius.edu.edumanagementsystem.dto.request.BasicRequests.MajorRequest;
import com.tzufucius.edu.edumanagementsystem.dto.vo.BasicVOs.ClassInfoVO;
import com.tzufucius.edu.edumanagementsystem.dto.vo.BasicVOs.CollegeVO;
import com.tzufucius.edu.edumanagementsystem.dto.vo.BasicVOs.CourseVO;
import com.tzufucius.edu.edumanagementsystem.dto.vo.BasicVOs.DepartmentVO;
import com.tzufucius.edu.edumanagementsystem.dto.vo.BasicVOs.MajorVO;
import com.tzufucius.edu.edumanagementsystem.entity.ClassInfo;
import com.tzufucius.edu.edumanagementsystem.entity.College;
import com.tzufucius.edu.edumanagementsystem.entity.Course;
import com.tzufucius.edu.edumanagementsystem.entity.Department;
import com.tzufucius.edu.edumanagementsystem.entity.Major;

public final class BasicMapper {
    private BasicMapper() {
    }

    public static College toEntity(CollegeRequest request) {
        College entity = new College();
        entity.setCollegeCode(request.collegeCode());
        entity.setCollegeName(request.collegeName());
        entity.setDescription(request.description());
        return entity;
    }

    public static CollegeVO toVO(College entity) {
        return new CollegeVO(entity.getId(), entity.getCollegeCode(), entity.getCollegeName(), entity.getDescription(),
                entity.getStatus(), entity.getCreatedAt(), entity.getUpdatedAt());
    }

    public static Department toEntity(DepartmentRequest request) {
        Department entity = new Department();
        entity.setCollegeId(request.collegeId());
        entity.setDepartmentCode(request.departmentCode());
        entity.setDepartmentName(request.departmentName());
        entity.setOfficeLocation(request.officeLocation());
        return entity;
    }

    public static DepartmentVO toVO(Department entity) {
        return new DepartmentVO(entity.getId(), entity.getCollegeId(), entity.getDepartmentCode(),
                entity.getDepartmentName(), entity.getOfficeLocation(), entity.getStatus(), entity.getCreatedAt(),
                entity.getUpdatedAt());
    }

    public static Major toEntity(MajorRequest request) {
        Major entity = new Major();
        entity.setCollegeId(request.collegeId());
        entity.setMajorCode(request.majorCode());
        entity.setMajorName(request.majorName());
        entity.setSchoolingYears(request.schoolingYears());
        entity.setDegreeType(request.degreeType());
        return entity;
    }

    public static MajorVO toVO(Major entity) {
        return new MajorVO(entity.getId(), entity.getCollegeId(), entity.getMajorCode(), entity.getMajorName(),
                entity.getSchoolingYears(), entity.getDegreeType(), entity.getStatus(), entity.getCreatedAt(),
                entity.getUpdatedAt());
    }

    public static ClassInfo toEntity(ClassInfoRequest request) {
        ClassInfo entity = new ClassInfo();
        entity.setMajorId(request.majorId());
        entity.setClassCode(request.classCode());
        entity.setClassName(request.className());
        entity.setEntranceYear(request.entranceYear());
        return entity;
    }

    public static ClassInfoVO toVO(ClassInfo entity) {
        return new ClassInfoVO(entity.getId(), entity.getMajorId(), entity.getClassCode(), entity.getClassName(),
                entity.getEntranceYear(), entity.getStatus(), entity.getCreatedAt(), entity.getUpdatedAt());
    }

    public static Course toEntity(CourseRequest request) {
        Course entity = new Course();
        entity.setCourseCode(request.courseCode());
        entity.setCourseName(request.courseName());
        entity.setCredit(request.credit());
        entity.setTotalHours(request.totalHours());
        entity.setCourseType(request.courseType());
        entity.setExamType(request.examType());
        return entity;
    }

    public static CourseVO toVO(Course entity) {
        return new CourseVO(entity.getId(), entity.getCourseCode(), entity.getCourseName(), entity.getCredit(),
                entity.getTotalHours(), entity.getCourseType(), entity.getExamType(), entity.getStatus(),
                entity.getCreatedAt(), entity.getUpdatedAt());
    }
}
