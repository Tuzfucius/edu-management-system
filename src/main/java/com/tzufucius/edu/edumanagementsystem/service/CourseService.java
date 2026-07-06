package com.tzufucius.edu.edumanagementsystem.service;

import com.tzufucius.edu.edumanagementsystem.dao.CourseDao;
import com.tzufucius.edu.edumanagementsystem.dto.request.CourseRequest;
import com.tzufucius.edu.edumanagementsystem.dto.vo.CourseVO;
import com.tzufucius.edu.edumanagementsystem.entity.Course;
import com.tzufucius.edu.edumanagementsystem.mapper.BasicMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseService {
    private final CourseDao courseDao;

    public CourseService(CourseDao courseDao) {
        this.courseDao = courseDao;
    }

    public List<Course> findAll() {
        return courseDao.findAll();
    }

    public List<CourseVO> findAllVO() {
        return findAll().stream().map(BasicMapper::toVO).toList();
    }

    public Course findById(Long id) {
        return requireCourse(id, "课程不存在");
    }

    public CourseVO findByIdVO(Long id) {
        return BasicMapper.toVO(findById(id));
    }

    public void addCourse(CourseRequest request) {
        addCourse(BasicMapper.toEntity(request));
    }

    public void addCourse(Course course) {
        checkRequiredFields(course);
        if (courseDao.countByCourseCode(course.getCourseCode()) > 0) {
            throw new RuntimeException("课程编号已存在");
        }
        if (courseDao.insert(course) != 1) {
            throw new RuntimeException("新增课程失败");
        }
    }

    public void updateCourse(Long id, CourseRequest request) {
        Course course = BasicMapper.toEntity(request);
        course.setId(id);
        updateCourse(course);
    }

    public void updateCourse(Course course) {
        Long id = course.getId();
        checkRequiredFields(course);
        requireCourse(id, "课程不存在，无法修改");
        if (courseDao.countByCourseCodeExcludeId(course.getCourseCode(), id) > 0) {
            throw new RuntimeException("课程编号已存在");
        }
        if (courseDao.update(course) != 1) {
            throw new RuntimeException("修改课程失败");
        }
    }

    public void deleteCourse(Long id) {
        requireCourse(id, "课程不存在，无法删除");
        if (courseDao.countTeachingTaskByCourseId(id) > 0) {
            throw new RuntimeException("该课程已有任课安排，不能删除");
        }
        if (courseDao.disableById(id) != 1) {
            throw new RuntimeException("删除课程失败");
        }
    }

    private Course requireCourse(Long id, String message) {
        if (id == null) {
            throw new RuntimeException("课程ID不能为空");
        }
        Course course = courseDao.findById(id);
        if (course == null) {
            throw new RuntimeException(message);
        }
        return course;
    }

    private void checkRequiredFields(Course course) {
        if (course == null) {
            throw new RuntimeException("课程信息不能为空");
        }
        if (course.getCourseCode() == null || course.getCourseCode().isBlank()) {
            throw new RuntimeException("课程编号不能为空");
        }
        if (course.getCourseName() == null || course.getCourseName().isBlank()) {
            throw new RuntimeException("课程名称不能为空");
        }
        if (course.getCredit() == null || course.getCredit().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("课程学分必须大于0");
        }
    }
}
