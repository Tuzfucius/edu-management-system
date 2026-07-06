package com.tzufucius.edu.edumanagementsystem.service;

import com.tzufucius.edu.edumanagementsystem.dao.CourseDao;
import com.tzufucius.edu.edumanagementsystem.dto.request.BasicRequests.CourseRequest;
import com.tzufucius.edu.edumanagementsystem.dto.vo.BasicVOs.CourseVO;
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

    public List<CourseVO> findAll() {
        return courseDao.findAll().stream().map(BasicMapper::toVO).toList();
    }

    public CourseVO findById(Long id) {
        return BasicMapper.toVO(requireCourse(id, "课程不存在"));
    }

    public void addCourse(CourseRequest request) {
        Course course = BasicMapper.toEntity(request);
        if (courseDao.countByCourseCode(course.getCourseCode()) > 0) {
            throw new RuntimeException("课程编号已存在");
        }
        if (courseDao.insert(course) != 1) {
            throw new RuntimeException("新增课程失败");
        }
    }

    public void updateCourse(Long id, CourseRequest request) {
        requireCourse(id, "课程不存在，无法修改");
        Course course = BasicMapper.toEntity(request);
        course.setId(id);
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
}
