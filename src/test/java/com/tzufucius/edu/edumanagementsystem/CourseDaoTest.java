package com.tzufucius.edu.edumanagementsystem;

import com.tzufucius.edu.edumanagementsystem.dao.CourseDao;
import com.tzufucius.edu.edumanagementsystem.entity.Course;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class CourseDaoTest {

    @Autowired
    private CourseDao courseDao;

    @Test
    void testFindAll() {
        List<Course> courses = courseDao.findAll();

        assertNotNull(courses);
    }

    @Test
    void testInsert() {
        Course course = createCourse("TEST-COURSE-DAO-001", "DAO测试课程");

        int rows = courseDao.insert(course);

        assertEquals(1, rows);
    }

    @Test
    void testFindById() {
        Course course = createCourse("TEST-COURSE-DAO-002", "DAO查询测试课程");
        courseDao.insert(course);

        Course inserted = courseDao.findAll().get(0);
        Course result = courseDao.findById(inserted.getId());

        assertNotNull(result);
        assertEquals(inserted.getId(), result.getId());
    }

    @Test
    void testCountByCourseCode() {
        Course course = createCourse("TEST-COURSE-DAO-003", "DAO编号统计测试课程");
        courseDao.insert(course);

        int count = courseDao.countByCourseCode("TEST-COURSE-DAO-003");

        assertEquals(1, count);
    }

    @Test
    void testCountByCourseCodeExcludeId() {
        Course course = createCourse("TEST-COURSE-DAO-004", "DAO排除自身编号测试课程");
        courseDao.insert(course);

        Course inserted = courseDao.findAll().get(0);
        int count = courseDao.countByCourseCodeExcludeId("TEST-COURSE-DAO-004", inserted.getId());

        assertEquals(0, count);
    }

    @Test
    void testDisableById() {
        Course course = createCourse("TEST-COURSE-DAO-005", "DAO逻辑删除测试课程");
        courseDao.insert(course);

        Course inserted = courseDao.findAll().get(0);
        int rows = courseDao.disableById(inserted.getId());

        assertEquals(1, rows);
    }

    @Test
    void testCountTeachingTaskByCourseId() {
        Course course = createCourse("TEST-COURSE-DAO-006", "DAO任课数量测试课程");
        courseDao.insert(course);

        Course inserted = courseDao.findAll().get(0);
        int count = courseDao.countTeachingTaskByCourseId(inserted.getId());

        assertEquals(0, count);
    }

    private Course createCourse(String code, String name) {
        Course course = new Course();
        course.setCourseCode(code);
        course.setCourseName(name);
        course.setCredit(new BigDecimal("3.0"));
        course.setTotalHours(48);
        course.setCourseType("必修");
        course.setExamType("考试");
        return course;
    }
}
