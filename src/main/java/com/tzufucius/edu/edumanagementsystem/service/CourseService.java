package com.tzufucius.edu.edumanagementsystem.service;

import com.tzufucius.edu.edumanagementsystem.dao.CourseDao;
import com.tzufucius.edu.edumanagementsystem.entity.Course;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CourseService {

    private final CourseDao courseDao;

    public CourseService(CourseDao courseDao) {
        this.courseDao = courseDao;
    }

    /**
     * 查询全部正常课程。
     *
     * @return 正常状态的课程列表
     */
    public List<Course> findAll() {
        return courseDao.findAll();
    }

    /**
     * 根据课程ID查询课程详情。
     *
     * @param id 课程ID
     * @return 课程详情
     * @throws RuntimeException 课程ID为空或课程不存在时抛出
     */
    public Course findById(Long id) {
        if (id == null) {
            throw new RuntimeException("课程ID不能为空");
        }

        Course course = courseDao.findById(id);
        if (course == null) {
            throw new RuntimeException("课程不存在");
        }

        return course;
    }

    /**
     * 新增课程，并校验必填字段和课程编号唯一性。
     *
     * @param course 待新增的课程信息
     * @throws RuntimeException 课程信息不合法、编号重复或新增失败时抛出
     */
    public void addCourse(Course course) {
        checkCourseForAdd(course);

        int exists = courseDao.countByCourseCode(course.getCourseCode());
        if (exists > 0) {
            throw new RuntimeException("课程编号已存在");
        }

        int rows = courseDao.insert(course);
        if (rows != 1) {
            throw new RuntimeException("新增课程失败");
        }
    }

    /**
     * 修改课程，并校验目标课程是否存在以及课程编号是否与其他课程重复。
     *
     * @param course 待修改的课程信息
     * @throws RuntimeException 课程信息不合法、课程不存在、编号重复或修改失败时抛出
     */
    public void updateCourse(Course course) {
        checkCourseForUpdate(course);

        Course oldCourse = courseDao.findById(course.getId());
        if (oldCourse == null) {
            throw new RuntimeException("课程不存在，无法修改");
        }

        int exists = courseDao.countByCourseCodeExcludeId(course.getCourseCode(), course.getId());
        if (exists > 0) {
            throw new RuntimeException("课程编号已存在");
        }

        int rows = courseDao.update(course);
        if (rows != 1) {
            throw new RuntimeException("修改课程失败");
        }
    }

    /**
     * 逻辑删除课程。删除前会校验课程是否存在，以及课程是否已有任课安排。
     *
     * @param id 课程ID
     * @throws RuntimeException 课程ID为空、课程不存在、仍有关联任课安排或删除失败时抛出
     */
    public void deleteCourse(Long id) {
        if (id == null) {
            throw new RuntimeException("课程ID不能为空");
        }

        Course course = courseDao.findById(id);
        if (course == null) {
            throw new RuntimeException("课程不存在，无法删除");
        }

        int teachingTaskCount = courseDao.countTeachingTaskByCourseId(id);
        if (teachingTaskCount > 0) {
            throw new RuntimeException("该课程已有任课安排，不能删除");
        }

        int rows = courseDao.disableById(id);
        if (rows != 1) {
            throw new RuntimeException("删除课程失败");
        }
    }

    /**
     * 校验新增课程需要的基础字段。
     *
     * @param course 待新增的课程信息
     * @throws RuntimeException 课程对象、课程编号、课程名称、学分或学时不合法时抛出
     */
    private void checkCourseForAdd(Course course) {
        if (course == null) {
            throw new RuntimeException("课程信息不能为空");
        }

        checkRequiredFields(course);
    }

    /**
     * 校验修改课程需要的基础字段。
     *
     * @param course 待修改的课程信息
     * @throws RuntimeException 课程对象、课程ID、课程编号、课程名称、学分或学时不合法时抛出
     */
    private void checkCourseForUpdate(Course course) {
        if (course == null) {
            throw new RuntimeException("课程信息不能为空");
        }

        if (course.getId() == null) {
            throw new RuntimeException("课程ID不能为空");
        }

        checkRequiredFields(course);
    }

    private void checkRequiredFields(Course course) {
        if (course.getCourseCode() == null || course.getCourseCode().isBlank()) {
            throw new RuntimeException("课程编号不能为空");
        }

        if (course.getCourseName() == null || course.getCourseName().isBlank()) {
            throw new RuntimeException("课程名称不能为空");
        }

        if (course.getCredit() == null || course.getCredit().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("课程学分必须大于0");
        }

        if (course.getTotalHours() != null && course.getTotalHours() <= 0) {
            throw new RuntimeException("课程学时必须大于0");
        }
    }
}
