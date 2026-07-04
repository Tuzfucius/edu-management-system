package com.tzufucius.edu.edumanagementsystem;

import com.tzufucius.edu.edumanagementsystem.entity.Course;
import com.tzufucius.edu.edumanagementsystem.service.CourseService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CourseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CourseService courseService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void listCourses() throws Exception {
        mockMvc.perform(get("/api/courses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void getCourse() throws Exception {
        Course course = createCourse("CTRL-COURSE-GET", "Controller查询课程");

        mockMvc.perform(get("/api/courses/{id}", course.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.courseCode").value("CTRL-COURSE-GET"));
    }

    @Test
    void createCourseSuccess() throws Exception {
        mockMvc.perform(post("/api/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"courseCode":"CTRL-COURSE-ADD","courseName":"Controller新增课程","credit":3.0,"totalHours":48,"courseType":"必修","examType":"考试"}
                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        assertOperationLog("课程管理", "INSERT");
    }

    @Test
    void createCourseWithoutNameShouldFail() throws Exception {
        mockMvc.perform(post("/api/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"courseCode":"CTRL-COURSE-NONAME","credit":3.0}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("课程名称不能为空"));
    }

    @Test
    void updateCourseSuccess() throws Exception {
        Course course = createCourse("CTRL-COURSE-UPD", "Controller修改前课程");

        mockMvc.perform(put("/api/courses/{id}", course.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"courseCode":"CTRL-COURSE-UPD","courseName":"Controller修改后课程","credit":3.5,"totalHours":56,"courseType":"选修","examType":"考查"}
                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        assertOperationLog("课程管理", "UPDATE");
    }

    @Test
    void deleteCourseSuccess() throws Exception {
        Course course = createCourse("CTRL-COURSE-DEL", "Controller删除课程");

        mockMvc.perform(delete("/api/courses/{id}", course.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        assertOperationLog("课程管理", "DISABLE");
    }

    private void assertOperationLog(String moduleName, String operationType) {
        Integer count = jdbcTemplate.queryForObject("""
                SELECT COUNT(*)
                FROM operation_log
                WHERE module_name = ? AND operation_type = ?
                """, Integer.class, moduleName, operationType);
        org.junit.jupiter.api.Assertions.assertTrue(count != null && count > 0);
    }

    private Course createCourse(String code, String name) {
        Course course = new Course();
        course.setCourseCode(code);
        course.setCourseName(name);
        course.setCredit(new BigDecimal("3.0"));
        course.setTotalHours(48);
        courseService.addCourse(course);
        return courseService.findAll().stream()
                .filter(item -> code.equals(item.getCourseCode()))
                .findFirst()
                .orElseThrow();
    }
}
