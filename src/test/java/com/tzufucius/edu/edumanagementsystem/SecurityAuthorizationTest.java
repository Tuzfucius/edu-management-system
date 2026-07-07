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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class SecurityAuthorizationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void unauthenticatedApiShouldReturn401() throws Exception {
        mockMvc.perform(get("/api/users")).andExpect(status().isUnauthorized()).andExpect(jsonPath("$.code").value(401));
        mockMvc.perform(get("/api/courses")).andExpect(status().isUnauthorized()).andExpect(jsonPath("$.code").value(401));
        mockMvc.perform(get("/api/student-courses")).andExpect(status().isUnauthorized()).andExpect(jsonPath("$.code").value(401));
    }

    @Test
    void nonAdminShouldNotAccessAdminApi() throws Exception {
        mockMvc.perform(get("/api/users").session(TestAuth.studentSession(9001L)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(403));
        mockMvc.perform(get("/api/users").session(TestAuth.teacherSession(9002L)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(403));
    }

    @Test
    void adminShouldAccessAdminApi() throws Exception {
        mockMvc.perform(get("/api/users").session(TestAuth.adminSession()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void unknownUserShouldNotFallbackLoginWithDefaultPassword() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"missing-auth-user","password":"123456","role":"ADMIN"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }

    @Test
    void studentShouldNotReadOtherStudentCourses() throws Exception {
        Fixture owner = createFixture("owner");
        Fixture other = createFixture("other");
        Long taskId = insertTeachingTask(other.courseId(), other.teacherId());
        Long otherStudentCourseId = insertStudentCourse(other.studentId(), taskId);

        mockMvc.perform(get("/api/student-courses")
                        .session(TestAuth.studentSession(owner.studentUserId()))
                        .param("studentId", other.studentId().toString()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(403));

        mockMvc.perform(get("/api/student-courses")
                        .session(TestAuth.studentSession(other.studentUserId()))
                        .param("studentId", other.studentId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id").value(otherStudentCourseId));
    }

    @Test
    void teacherShouldNotUpdateOtherTeacherScore() throws Exception {
        Fixture owner = createFixture("teacher-owner");
        Fixture other = createFixture("teacher-other");
        Long taskId = insertTeachingTask(other.courseId(), other.teacherId());
        Long studentCourseId = insertStudentCourse(other.studentId(), taskId);

        mockMvc.perform(put("/api/student-courses/{id}/score", studentCourseId)
                        .session(TestAuth.teacherSession(owner.teacherUserId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"score":90,"remark":"越权录入"}
                                """))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(403));

        mockMvc.perform(put("/api/student-courses/{id}/score", studentCourseId)
                        .session(TestAuth.teacherSession(other.teacherUserId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"score":90,"remark":"正常录入"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    private Fixture createFixture(String prefix) {
        String suffix = prefix.substring(0, Math.min(prefix.length(), 3)) + Long.toString(System.nanoTime(), 36);
        Long collegeId = insertCollege(suffix);
        Long majorId = insertMajor(suffix, collegeId);
        Long classId = insertClass(suffix, majorId);
        Long departmentId = insertDepartment(suffix, collegeId);
        Long studentUserId = insertUser("student-" + suffix, "STUDENT");
        Long teacherUserId = insertUser("teacher-" + suffix, "TEACHER");
        Long studentId = insertStudent("S" + suffix, studentUserId, classId);
        Long teacherId = insertTeacher("T" + suffix, teacherUserId, departmentId);
        Long courseId = insertCourse(suffix);
        return new Fixture(studentId, teacherId, courseId, studentUserId, teacherUserId);
    }

    private Long insertUser(String username, String role) {
        jdbcTemplate.update("INSERT INTO sys_user(username, password, role, status) VALUES (?, '123456', ?, 1)", username, role);
        return queryId("sys_user", "username", username);
    }

    private Long insertCollege(String suffix) {
        jdbcTemplate.update("INSERT INTO college(college_code, college_name, status) VALUES (?, ?, 1)", "COL-" + suffix, "测试学院" + suffix);
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

    private Long insertTeachingTask(Long courseId, Long teacherId) {
        jdbcTemplate.update("""
                INSERT INTO teaching_task(course_id, teacher_id, semester, weekday, start_section,
                                          end_section, weeks, classroom, capacity, selected_count, task_status)
                VALUES (?, ?, '2025-2026-1', 2, 3, 4, '1-16周', 'A302', 60, 0, 1)
                """, courseId, teacherId);
        return jdbcTemplate.queryForObject("""
                SELECT id FROM teaching_task
                WHERE course_id = ? AND teacher_id = ?
                ORDER BY id DESC LIMIT 1
                """, Long.class, courseId, teacherId);
    }

    private Long insertStudentCourse(Long studentId, Long teachingTaskId) {
        jdbcTemplate.update("""
                INSERT INTO student_course(student_id, teaching_task_id, grade_status, status)
                VALUES (?, ?, 0, 1)
                """, studentId, teachingTaskId);
        return jdbcTemplate.queryForObject("""
                SELECT id FROM student_course
                WHERE student_id = ? AND teaching_task_id = ?
                ORDER BY id DESC LIMIT 1
                """, Long.class, studentId, teachingTaskId);
    }

    private Long queryId(String tableName, String columnName, String value) {
        return jdbcTemplate.queryForObject(
                "SELECT id FROM " + tableName + " WHERE " + columnName + " = ? ORDER BY id DESC LIMIT 1",
                Long.class,
                value
        );
    }

    private record Fixture(Long studentId, Long teacherId, Long courseId, Long studentUserId, Long teacherUserId) {
    }
}
