package com.tzufucius.edu.edumanagementsystem;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AcademicBusinessControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void userCrudShouldWork() throws Exception {
        String suffix = String.valueOf(System.nanoTime());

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"u-%s","password":"123456","role":"STUDENT"}
                                """.formatted(suffix)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        Long userId = queryId("sys_user", "username", "u-" + suffix);

        mockMvc.perform(get("/api/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.username").value("u-" + suffix));

        mockMvc.perform(put("/api/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"u-%s-updated","role":"TEACHER"}
                                """.formatted(suffix)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        mockMvc.perform(delete("/api/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void studentTeacherTeachingTaskAndGradeFlowShouldWork() throws Exception {
        Fixture fixture = createFixture();

        mockMvc.perform(get("/api/students/{id}", fixture.studentId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.studentNo").value(fixture.studentNo()));

        mockMvc.perform(get("/api/teachers/{id}", fixture.teacherId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.teacherNo").value(fixture.teacherNo()));

        mockMvc.perform(post("/api/teaching-tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"courseId":%d,"teacherId":%d,"semester":"2025-2026-1","weekday":2,"startSection":3,"endSection":4,"weeks":"1-16周","classroom":"A302","capacity":2}
                                """.formatted(fixture.courseId(), fixture.teacherId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        Long taskId = jdbcTemplate.queryForObject("""
                SELECT id FROM teaching_task
                WHERE course_id = ? AND teacher_id = ? AND semester = '2025-2026-1'
                ORDER BY id DESC LIMIT 1
                """, Long.class, fixture.courseId(), fixture.teacherId());

        mockMvc.perform(post("/api/student-courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"studentId":%d,"teachingTaskId":%d}
                                """.formatted(fixture.studentId(), taskId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
        assertOperationLog("选课管理", "SELECT_COURSE");

        Long studentCourseId = jdbcTemplate.queryForObject("""
                SELECT id FROM student_course
                WHERE student_id = ? AND teaching_task_id = ?
                """, Long.class, fixture.studentId(), taskId);

        mockMvc.perform(put("/api/student-courses/{id}/score", studentCourseId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"score":88,"remark":"测试录入"}
                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
        assertOperationLog("成绩管理", "UPDATE_SCORE");

        mockMvc.perform(get("/api/student-courses").param("studentId", fixture.studentId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].score").value(88));

        mockMvc.perform(delete("/api/student-courses/{id}", studentCourseId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));

        mockMvc.perform(delete("/api/student-courses/{id}/score", studentCourseId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
        assertOperationLog("成绩管理", "REVOKE_SCORE");
        org.junit.jupiter.api.Assertions.assertEquals(0, jdbcTemplate.queryForObject(
                "SELECT grade_status FROM student_course WHERE id = ?",
                Integer.class,
                studentCourseId
        ));
        org.junit.jupiter.api.Assertions.assertNull(jdbcTemplate.queryForObject(
                "SELECT score FROM student_course WHERE id = ?",
                java.math.BigDecimal.class,
                studentCourseId
        ));

        mockMvc.perform(delete("/api/student-courses/{id}", studentCourseId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
        assertOperationLog("选课管理", "DROP_COURSE");
    }

    @Test
    void selectCourseShouldRejectTimeConflict() throws Exception {
        Fixture fixture = createFixture();

        Long firstTaskId = insertTeachingTask(fixture.courseId(), fixture.teacherId(), 2, 3, 4);
        mockMvc.perform(post("/api/student-courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"studentId":%d,"teachingTaskId":%d}
                                """.formatted(fixture.studentId(), firstTaskId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        Long conflictTaskId = insertTeachingTask(fixture.courseId(), fixture.teacherId(), 2, 4, 5);
        mockMvc.perform(post("/api/student-courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"studentId":%d,"teachingTaskId":%d}
                                """.formatted(fixture.studentId(), conflictTaskId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400));
    }

    @Test
    void teacherStudentAndReportsShouldWork() throws Exception {
        Fixture fixture = createFixture();

        mockMvc.perform(post("/api/teacher-students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"teacherId":%d,"studentId":%d,"guideType":"课程设计指导","startDate":"2026-01-01"}
                                """.formatted(fixture.teacherId(), fixture.studentId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        mockMvc.perform(get("/api/teacher-students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        mockMvc.perform(get("/api/reports/overview"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.studentCount").exists());

        mockMvc.perform(get("/api/reports/college-students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    private Fixture createFixture() {
        String suffix = String.valueOf(System.nanoTime());
        Long collegeId = insertCollege(suffix);
        Long majorId = insertMajor(suffix, collegeId);
        Long classId = insertClass(suffix, majorId);
        Long departmentId = insertDepartment(suffix, collegeId);
        Long studentUserId = insertUser("student-" + suffix, "STUDENT");
        Long teacherUserId = insertUser("teacher-" + suffix, "TEACHER");
        String studentNo = "S" + suffix;
        String teacherNo = "T" + suffix;
        Long studentId = insertStudent(studentNo, studentUserId, classId);
        Long teacherId = insertTeacher(teacherNo, teacherUserId, departmentId);
        Long courseId = insertCourse(suffix);
        return new Fixture(studentId, teacherId, courseId, studentNo, teacherNo);
    }

    private Long insertUser(String username, String role) {
        jdbcTemplate.update("""
                INSERT INTO sys_user(username, password, role, status)
                VALUES (?, '123456', ?, 1)
                """, username, role);
        return queryId("sys_user", "username", username);
    }

    private Long insertCollege(String suffix) {
        jdbcTemplate.update("""
                INSERT INTO college(college_code, college_name, status)
                VALUES (?, ?, 1)
                """, "COL-" + suffix, "测试学院" + suffix);
        return queryId("college", "college_code", "COL-" + suffix);
    }

    private Long insertMajor(String suffix, Long collegeId) {
        jdbcTemplate.update("""
                INSERT INTO major(college_id, major_code, major_name, schooling_years, degree_type, status)
                VALUES (?, ?, ?, 4, '工学', 1)
                """, collegeId, "MAJ-" + suffix, "测试专业" + suffix);
        return queryId("major", "major_code", "MAJ-" + suffix);
    }

    private Long insertClass(String suffix, Long majorId) {
        jdbcTemplate.update("""
                INSERT INTO class_info(major_id, class_code, class_name, entrance_year, status)
                VALUES (?, ?, ?, 2025, 1)
                """, majorId, "CLS-" + suffix, "测试班级" + suffix);
        return queryId("class_info", "class_code", "CLS-" + suffix);
    }

    private Long insertDepartment(String suffix, Long collegeId) {
        jdbcTemplate.update("""
                INSERT INTO department(college_id, department_code, department_name, office_location, status)
                VALUES (?, ?, ?, 'A101', 1)
                """, collegeId, "DEP-" + suffix, "测试教研室" + suffix);
        return queryId("department", "department_code", "DEP-" + suffix);
    }

    private Long insertStudent(String studentNo, Long userId, Long classId) {
        jdbcTemplate.update("""
                INSERT INTO student(user_id, class_id, student_no, student_name, gender, birth_date, phone, email, enrollment_year, status)
                VALUES (?, ?, ?, '测试学生', 'M', ?, '13800000000', 'student@test.local', 2025, 1)
                """, userId, classId, studentNo, LocalDate.of(2005, 1, 1));
        return queryId("student", "student_no", studentNo);
    }

    private Long insertTeacher(String teacherNo, Long userId, Long departmentId) {
        jdbcTemplate.update("""
                INSERT INTO teacher(user_id, department_id, teacher_no, teacher_name, gender, title, phone, email, status)
                VALUES (?, ?, ?, '测试教师', 'M', '讲师', '13900000000', 'teacher@test.local', 1)
                """, userId, departmentId, teacherNo);
        return queryId("teacher", "teacher_no", teacherNo);
    }

    private Long insertCourse(String suffix) {
        jdbcTemplate.update("""
                INSERT INTO course(course_code, course_name, credit, total_hours, course_type, exam_type, status)
                VALUES (?, ?, 3.0, 48, '必修', '考试', 1)
                """, "COUR-" + suffix, "测试课程" + suffix);
        return queryId("course", "course_code", "COUR-" + suffix);
    }

    private Long insertTeachingTask(Long courseId, Long teacherId, int weekday, int startSection, int endSection) {
        jdbcTemplate.update("""
                INSERT INTO teaching_task(course_id, teacher_id, semester, weekday, start_section,
                                          end_section, weeks, classroom, capacity, selected_count, task_status)
                VALUES (?, ?, '2025-2026-1', ?, ?, ?, '1-16周', 'A302', 60, 0, 1)
                """, courseId, teacherId, weekday, startSection, endSection);
        return jdbcTemplate.queryForObject("""
                SELECT id
                FROM teaching_task
                WHERE course_id = ? AND teacher_id = ? AND weekday = ? AND start_section = ? AND end_section = ?
                ORDER BY id DESC LIMIT 1
                """, Long.class, courseId, teacherId, weekday, startSection, endSection);
    }

    private void assertOperationLog(String moduleName, String operationType) {
        Integer count = jdbcTemplate.queryForObject("""
                SELECT COUNT(*)
                FROM operation_log
                WHERE module_name = ? AND operation_type = ?
                """, Integer.class, moduleName, operationType);
        org.junit.jupiter.api.Assertions.assertTrue(count != null && count > 0);
    }

    private Long queryId(String tableName, String columnName, String value) {
        return jdbcTemplate.queryForObject(
                "SELECT id FROM " + tableName + " WHERE " + columnName + " = ? ORDER BY id DESC LIMIT 1",
                Long.class,
                value
        );
    }

    private record Fixture(Long studentId, Long teacherId, Long courseId, String studentNo, String teacherNo) {
    }
}
