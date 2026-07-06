package com.tzufucius.edu.edumanagementsystem;

import com.tzufucius.edu.edumanagementsystem.common.SemesterUtils;
import com.tzufucius.edu.edumanagementsystem.dto.request.CourseRequest;
import com.tzufucius.edu.edumanagementsystem.dto.vo.CourseVO;
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
    void listAndGetCourse() throws Exception {
        CourseVO course = create("COURSE-CTRL-GET");
        mockMvc.perform(get("/api/courses")).andExpect(status().isOk()).andExpect(jsonPath("$.code").value(200));
        mockMvc.perform(get("/api/courses/{id}", course.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.courseCode").value("COURSE-CTRL-GET"));
    }

    @Test
    void createUpdateDeleteCourse() throws Exception {
        mockMvc.perform(post("/api/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"courseCode\":\"COURSE-CTRL-ADD\",\"courseName\":\"Add\",\"credit\":2.5,\"totalHours\":40,\"courseType\":\"required\",\"examType\":\"exam\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
        CourseVO course = courseService.findAll().stream().filter(item -> "COURSE-CTRL-ADD".equals(item.courseCode())).findFirst().orElseThrow();
        mockMvc.perform(put("/api/courses/{id}", course.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"courseCode\":\"COURSE-CTRL-UPD\",\"courseName\":\"Updated\",\"credit\":3,\"totalHours\":48,\"courseType\":\"required\",\"examType\":\"exam\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
        mockMvc.perform(delete("/api/courses/{id}", course.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void courseStatusAndReferenceRulesShouldWork() throws Exception {
        String suffix = Long.toString(System.nanoTime(), 36);
        CourseVO course = create("CST-" + suffix);

        mockMvc.perform(patch("/api/courses/{id}/disable", course.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
        mockMvc.perform(get("/api/courses/{id}", course.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value(0))
                .andExpect(jsonPath("$.data.canDisableInCurrentSemester").value(true));

        mockMvc.perform(patch("/api/courses/{id}/enable", course.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
        mockMvc.perform(get("/api/courses/{id}", course.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value(1));

        Long teacherId = insertTeacher(suffix);
        insertTeachingTask(course.id(), teacherId, SemesterUtils.currentSemester());

        mockMvc.perform(get("/api/courses/{id}", course.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.hasCurrentSemesterTask").value(true))
                .andExpect(jsonPath("$.data.canDisableInCurrentSemester").value(false));
        mockMvc.perform(patch("/api/courses/{id}/disable", course.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
        mockMvc.perform(delete("/api/courses/{id}", course.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }

    private CourseVO create(String code) {
        courseService.addCourse(new CourseRequest(code, code, new BigDecimal("2.5"), 40, "required", "exam"));
        return courseService.findAll().stream().filter(item -> code.equals(item.courseCode())).findFirst().orElseThrow();
    }

    private Long insertTeacher(String suffix) {
        jdbcTemplate.update("""
                INSERT INTO college(college_code, college_name, status)
                VALUES (?, ?, 1)
                """, "CC-" + suffix, "测试学院" + suffix);
        Long collegeId = queryId("college", "college_code", "CC-" + suffix);
        jdbcTemplate.update("""
                INSERT INTO department(college_id, department_code, department_name, office_location, status)
                VALUES (?, ?, ?, 'A101', 1)
                """, collegeId, "CD-" + suffix, "测试教研室" + suffix);
        Long departmentId = queryId("department", "department_code", "CD-" + suffix);
        jdbcTemplate.update("""
                INSERT INTO sys_user(username, password, role, status)
                VALUES (?, '123456', 'TEACHER', 1)
                """, "course-teacher-" + suffix);
        Long userId = queryId("sys_user", "username", "course-teacher-" + suffix);
        jdbcTemplate.update("""
                INSERT INTO teacher(user_id, department_id, teacher_no, teacher_name, gender, title, status)
                VALUES (?, ?, ?, '测试教师', 'M', '讲师', 1)
                """, userId, departmentId, "CT-" + suffix);
        return queryId("teacher", "teacher_no", "CT-" + suffix);
    }

    private void insertTeachingTask(Long courseId, Long teacherId, String semester) {
        jdbcTemplate.update("""
                INSERT INTO teaching_task(course_id, teacher_id, semester, weekday, start_section,
                                          end_section, weeks, classroom, capacity, selected_count, task_status)
                VALUES (?, ?, ?, 2, 3, 4, '1-16周', 'A302', 60, 0, 1)
                """, courseId, teacherId, semester);
    }

    private Long queryId(String tableName, String columnName, String value) {
        return jdbcTemplate.queryForObject(
                "SELECT id FROM " + tableName + " WHERE " + columnName + " = ? ORDER BY id DESC LIMIT 1",
                Long.class,
                value
        );
    }
}
