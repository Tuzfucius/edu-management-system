package com.tzufucius.edu.edumanagementsystem;

import com.tzufucius.edu.edumanagementsystem.dao.CollegeDao;
import com.tzufucius.edu.edumanagementsystem.dao.CourseDao;
import com.tzufucius.edu.edumanagementsystem.dao.DepartmentDao;
import com.tzufucius.edu.edumanagementsystem.entity.College;
import com.tzufucius.edu.edumanagementsystem.entity.Course;
import com.tzufucius.edu.edumanagementsystem.entity.Department;
import com.tzufucius.edu.edumanagementsystem.service.CourseService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class CourseServiceTest {

    @Autowired
    private CourseService courseService;

    @Autowired
    private CourseDao courseDao;

    @Autowired
    private DepartmentDao departmentDao;

    @Autowired
    private CollegeDao collegeDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void testFindAll() {
        assertNotNull(courseService.findAll());
    }

    @Test
    void testAddCourseSuccess() {
        Course course = createCourse("TEST-COURSE-SERVICE-001", "Service测试课程");

        assertDoesNotThrow(() -> courseService.addCourse(course));
    }

    @Test
    void testAddCourseWithoutCodeShouldFail() {
        Course course = createCourse("TEST-COURSE-SERVICE-002", "无编号课程");
        course.setCourseCode(null);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> courseService.addCourse(course)
        );

        assertEquals("课程编号不能为空", exception.getMessage());
    }

    @Test
    void testAddCourseWithoutNameShouldFail() {
        Course course = createCourse("TEST-COURSE-SERVICE-003", "无名称课程");
        course.setCourseName(null);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> courseService.addCourse(course)
        );

        assertEquals("课程名称不能为空", exception.getMessage());
    }

    @Test
    void testAddCourseWithoutCreditShouldFail() {
        Course course = createCourse("TEST-COURSE-SERVICE-004", "无学分课程");
        course.setCredit(null);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> courseService.addCourse(course)
        );

        assertEquals("课程学分必须大于0", exception.getMessage());
    }

    @Test
    void testAddCourseDuplicateCodeShouldFail() {
        Course course1 = createCourse("TEST-COURSE-SERVICE-005", "第一个课程");
        Course course2 = createCourse("TEST-COURSE-SERVICE-005", "第二个课程");
        courseService.addCourse(course1);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> courseService.addCourse(course2)
        );

        assertEquals("课程编号已存在", exception.getMessage());
    }

    @Test
    void testUpdateCourseDuplicateCodeShouldFail() {
        Course course1 = createCourse("TEST-COURSE-SERVICE-006", "第一个待修改课程");
        Course course2 = createCourse("TEST-COURSE-SERVICE-007", "第二个待修改课程");
        courseService.addCourse(course1);
        courseService.addCourse(course2);

        Course firstCourse = courseService.findAll().get(1);
        Course secondCourse = courseService.findAll().get(0);
        secondCourse.setCourseCode(firstCourse.getCourseCode());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> courseService.updateCourse(secondCourse)
        );

        assertEquals("课程编号已存在", exception.getMessage());
    }

    @Test
    void testDeleteMissingCourseShouldFail() {
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> courseService.deleteCourse(Long.MAX_VALUE)
        );

        assertEquals("课程不存在，无法删除", exception.getMessage());
    }

    @Test
    void testDeleteCourseWithTeachingTaskShouldFail() {
        Course course = createCourse("TEST-COURSE-SERVICE-008", "有关联任课课程");
        courseService.addCourse(course);
        Course inserted = courseService.findAll().get(0);
        Long teacherId = insertTeacher();

        jdbcTemplate.update(
                """
                        INSERT INTO teaching_task(course_id, teacher_id, semester, weekday, start_section, end_section, capacity, selected_count, task_status)
                        VALUES (?, ?, ?, ?, ?, ?, ?, 0, 1)
                        """,
                inserted.getId(),
                teacherId,
                "2025-2026-1",
                1,
                1,
                2,
                60
        );

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> courseService.deleteCourse(inserted.getId())
        );

        assertEquals("该课程已有任课安排，不能删除", exception.getMessage());
    }

    @Test
    void testDeleteCourseSuccess() {
        Course course = createCourse("TEST-COURSE-SERVICE-009", "逻辑删除课程");
        courseService.addCourse(course);

        Course inserted = courseService.findAll().get(0);

        assertDoesNotThrow(() -> courseService.deleteCourse(inserted.getId()));
        assertNull(courseDao.findById(inserted.getId()));
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

    private Long insertTeacher() {
        College college = new College();
        college.setCollegeCode("C" + System.nanoTime());
        college.setCollegeName("任课测试学院");
        college.setDescription("用于课程删除校验");
        collegeDao.insert(college);

        Department department = new Department();
        department.setCollegeId(collegeDao.findAll().get(0).getId());
        department.setDepartmentCode("D" + System.nanoTime());
        department.setDepartmentName("任课测试教研室");
        department.setOfficeLocation("测试楼 301");
        departmentDao.insert(department);

        Long userId = insertTeacherUser();
        jdbcTemplate.update(
                """
                        INSERT INTO teacher(user_id, department_id, teacher_no, teacher_name, gender, title, status)
                        VALUES (?, ?, ?, ?, ?, ?, 1)
                        """,
                userId,
                departmentDao.findAll().get(0).getId(),
                "T" + System.nanoTime(),
                "课程删除校验教师",
                "M",
                "讲师"
        );

        return jdbcTemplate.queryForObject(
                "SELECT id FROM teacher WHERE user_id = ?",
                Long.class,
                userId
        );
    }

    private Long insertTeacherUser() {
        String username = "u" + System.nanoTime();
        jdbcTemplate.update(
                "INSERT INTO sys_user(username, password, role, status) VALUES (?, ?, 'TEACHER', 1)",
                username,
                "123456"
        );

        return jdbcTemplate.queryForObject(
                "SELECT id FROM sys_user WHERE username = ?",
                Long.class,
                username
        );
    }
}
