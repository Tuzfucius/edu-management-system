package com.tzufucius.edu.edumanagementsystem.service;

import com.tzufucius.edu.edumanagementsystem.common.SemesterUtils;
import com.tzufucius.edu.edumanagementsystem.dao.CourseDao;
import com.tzufucius.edu.edumanagementsystem.dto.request.CourseRequest;
import com.tzufucius.edu.edumanagementsystem.dto.vo.CourseVO;
import com.tzufucius.edu.edumanagementsystem.entity.Course;
import com.tzufucius.edu.edumanagementsystem.mapper.BasicMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CourseService {
    private final CourseDao courseDao;

    public CourseService(CourseDao courseDao) {
        this.courseDao = courseDao;
    }

    public List<CourseVO> findAll() {
        String semester = SemesterUtils.currentSemester();
        return courseDao.findAll().stream()
                .map(course -> BasicMapper.toVO(course, courseDao.countTeachingTaskByCourseIdAndSemester(course.getId(), semester)))
                .toList();
    }

    public CourseVO findById(Long id) {
        Course course = requireCourse(id);
        return BasicMapper.toVO(course, courseDao.countTeachingTaskByCourseIdAndSemester(course.getId(), SemesterUtils.currentSemester()));
    }

    public void addCourse(CourseRequest request) {
        Course course = BasicMapper.toEntity(request);
        checkRequiredFields(course);
        if (courseDao.countByCourseCode(course.getCourseCode()) > 0) {
            throw new RuntimeException("Course code already exists");
        }
        if (courseDao.insert(course) != 1) {
            throw new RuntimeException("Failed to create course");
        }
    }

    public void updateCourse(Long id, CourseRequest request) {
        Course course = BasicMapper.toEntity(request);
        course.setId(id);
        checkRequiredFields(course);
        requireCourse(id);
        if (courseDao.countByCourseCodeExcludeId(course.getCourseCode(), id) > 0) {
            throw new RuntimeException("Course code already exists");
        }
        if (courseDao.update(course) != 1) {
            throw new RuntimeException("Failed to update course");
        }
    }

    public void deleteCourse(Long id) {
        hardDeleteCourse(id);
    }

    public void disableCourse(Long id) {
        requireCourse(id);
        if (courseDao.countTeachingTaskByCourseIdAndSemester(id, SemesterUtils.currentSemester()) > 0) {
            throw new RuntimeException("Course has teaching tasks in current semester");
        }
        if (courseDao.disableById(id) != 1) {
            throw new RuntimeException("Failed to disable course");
        }
    }

    public void enableCourse(Long id) {
        requireCourse(id);
        if (courseDao.enableById(id) != 1) {
            throw new RuntimeException("Failed to enable course");
        }
    }

    public void hardDeleteCourse(Long id) {
        requireCourse(id);
        if (courseDao.countTeachingTaskByCourseId(id) > 0 || courseDao.countStudentCourseByCourseId(id) > 0) {
            throw new RuntimeException("Course has historical teaching or selection records and cannot be deleted");
        }
        if (courseDao.deleteById(id) != 1) {
            throw new RuntimeException("Failed to delete course");
        }
    }

    private Course requireCourse(Long id) {
        if (id == null) {
            throw new RuntimeException("Course id is required");
        }
        Course course = courseDao.findById(id);
        if (course == null) {
            throw new RuntimeException("Course does not exist");
        }
        return course;
    }

    private void checkRequiredFields(Course course) {
        if (course == null || course.getCourseCode() == null || course.getCourseCode().isBlank()
                || course.getCourseName() == null || course.getCourseName().isBlank()
                || course.getCredit() == null || course.getCredit().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Required course fields are missing");
        }
    }
}
