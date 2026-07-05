package com.tzufucius.edu.edumanagementsystem.dao;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Repository
public class AcademicBusinessDao {

    private final JdbcTemplate jdbcTemplate;

    public AcademicBusinessDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Map<String, Object> findUserForLogin(String username, String role) {
        String sql = """
                SELECT u.id, u.username, u.password, u.role, u.status,
                       COALESCE(t.teacher_name, s.student_name, u.username) AS displayName
                FROM sys_user u
                LEFT JOIN teacher t ON t.user_id = u.id AND t.status = 1
                LEFT JOIN student s ON s.user_id = u.id AND s.status = 1
                WHERE u.username = ? AND u.role = ? AND u.status = 1
                """;
        List<Map<String, Object>> users = jdbcTemplate.queryForList(sql, username, role);
        return users.isEmpty() ? null : users.get(0);
    }

    public void updateLastLogin(Long id) {
        jdbcTemplate.update("UPDATE sys_user SET last_login_at = NOW() WHERE id = ?", id);
    }

    public List<Map<String, Object>> listUsers() {
        return jdbcTemplate.queryForList("""
                SELECT id, username, role, status, last_login_at AS lastLoginAt,
                       created_at AS createdAt, updated_at AS updatedAt
                FROM sys_user
                ORDER BY id DESC
                """);
    }

    public Map<String, Object> findUserById(Long id) {
        return findOne("""
                SELECT id, username, role, status, last_login_at AS lastLoginAt,
                       created_at AS createdAt, updated_at AS updatedAt
                FROM sys_user
                WHERE id = ?
                """, id);
    }

    public int insertUser(Map<String, Object> user) {
        return jdbcTemplate.update("""
                INSERT INTO sys_user(username, password, role, status)
                VALUES (?, ?, ?, COALESCE(?, 1))
                """, user.get("username"), user.get("password"), user.get("role"), user.get("status"));
    }

    public int updateUser(Long id, Map<String, Object> user) {
        Object password = user.get("password");
        if (password == null || password.toString().isBlank()) {
            return jdbcTemplate.update("""
                    UPDATE sys_user
                    SET username = ?, role = ?, status = COALESCE(?, status)
                    WHERE id = ?
                    """, user.get("username"), user.get("role"), user.get("status"), id);
        }
        return jdbcTemplate.update("""
                UPDATE sys_user
                SET username = ?, password = ?, role = ?, status = COALESCE(?, status)
                WHERE id = ?
                """, user.get("username"), password, user.get("role"), user.get("status"), id);
    }

    public int disableUser(Long id) {
        return jdbcTemplate.update("UPDATE sys_user SET status = 0 WHERE id = ?", id);
    }

    public int countUsername(String username, Long excludeId) {
        if (excludeId == null) {
            return count("SELECT COUNT(*) FROM sys_user WHERE username = ?", username);
        }
        return count("SELECT COUNT(*) FROM sys_user WHERE username = ? AND id <> ?", username, excludeId);
    }

    public List<Map<String, Object>> listStudents() {
        return jdbcTemplate.queryForList("""
                SELECT s.id, s.user_id AS userId, s.class_id AS classId, s.student_no AS studentNo,
                       s.student_name AS studentName, s.gender, s.birth_date AS birthDate, s.phone, s.email,
                       s.enrollment_year AS enrollmentYear, s.status,
                       ci.class_name AS className, m.major_name AS majorName, c.college_name AS collegeName,
                       u.username
                FROM student s
                LEFT JOIN sys_user u ON u.id = s.user_id
                LEFT JOIN class_info ci ON ci.id = s.class_id
                LEFT JOIN major m ON m.id = ci.major_id
                LEFT JOIN college c ON c.id = m.college_id
                WHERE s.status = 1
                ORDER BY s.id DESC
                """);
    }

    public Map<String, Object> findStudentById(Long id) {
        return findOne("""
                SELECT s.id, s.user_id AS userId, s.class_id AS classId, s.student_no AS studentNo,
                       s.student_name AS studentName, s.gender, s.birth_date AS birthDate, s.phone, s.email,
                       s.enrollment_year AS enrollmentYear, s.status,
                       ci.class_name AS className, m.major_name AS majorName, c.college_name AS collegeName,
                       u.username
                FROM student s
                LEFT JOIN sys_user u ON u.id = s.user_id
                LEFT JOIN class_info ci ON ci.id = s.class_id
                LEFT JOIN major m ON m.id = ci.major_id
                LEFT JOIN college c ON c.id = m.college_id
                WHERE s.id = ? AND s.status = 1
                """, id);
    }

    public Map<String, Object> findStudentByUserId(Long userId) {
        return findOne("""
                SELECT s.id, s.user_id AS userId, s.class_id AS classId, s.student_no AS studentNo,
                       s.student_name AS studentName, s.gender, s.phone, s.email,
                       s.enrollment_year AS enrollmentYear, ci.class_name AS className,
                       m.major_name AS majorName, c.college_name AS collegeName
                FROM student s
                LEFT JOIN class_info ci ON ci.id = s.class_id
                LEFT JOIN major m ON m.id = ci.major_id
                LEFT JOIN college c ON c.id = m.college_id
                WHERE s.user_id = ? AND s.status = 1
                """, userId);
    }

    public int insertStudent(Map<String, Object> student) {
        return jdbcTemplate.update("""
                INSERT INTO student(user_id, class_id, student_no, student_name, gender, birth_date,
                                    phone, email, enrollment_year, status)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, 1)
                """, student.get("userId"), student.get("classId"), student.get("studentNo"),
                student.get("studentName"), student.get("gender"), student.get("birthDate"),
                student.get("phone"), student.get("email"), student.get("enrollmentYear"));
    }

    public int updateStudent(Long id, Map<String, Object> student) {
        return jdbcTemplate.update("""
                UPDATE student
                SET user_id = ?, class_id = ?, student_no = ?, student_name = ?, gender = ?,
                    birth_date = ?, phone = ?, email = ?, enrollment_year = ?
                WHERE id = ? AND status = 1
                """, student.get("userId"), student.get("classId"), student.get("studentNo"),
                student.get("studentName"), student.get("gender"), student.get("birthDate"),
                student.get("phone"), student.get("email"), student.get("enrollmentYear"), id);
    }

    public int disableStudent(Long id) {
        return jdbcTemplate.update("UPDATE student SET status = 0 WHERE id = ?", id);
    }

    public int countStudentNo(String studentNo, Long excludeId) {
        if (excludeId == null) {
            return count("SELECT COUNT(*) FROM student WHERE student_no = ?", studentNo);
        }
        return count("SELECT COUNT(*) FROM student WHERE student_no = ? AND id <> ?", studentNo, excludeId);
    }

    public List<Map<String, Object>> listTeachers() {
        return jdbcTemplate.queryForList("""
                SELECT t.id, t.user_id AS userId, t.department_id AS departmentId, t.teacher_no AS teacherNo,
                       t.teacher_name AS teacherName, t.gender, t.title, t.phone, t.email, t.status,
                       d.department_name AS departmentName, c.college_name AS collegeName, u.username
                FROM teacher t
                LEFT JOIN sys_user u ON u.id = t.user_id
                LEFT JOIN department d ON d.id = t.department_id
                LEFT JOIN college c ON c.id = d.college_id
                WHERE t.status = 1
                ORDER BY t.id DESC
                """);
    }

    public Map<String, Object> findTeacherById(Long id) {
        return findOne("""
                SELECT t.id, t.user_id AS userId, t.department_id AS departmentId, t.teacher_no AS teacherNo,
                       t.teacher_name AS teacherName, t.gender, t.title, t.phone, t.email, t.status,
                       d.department_name AS departmentName, c.college_name AS collegeName, u.username
                FROM teacher t
                LEFT JOIN sys_user u ON u.id = t.user_id
                LEFT JOIN department d ON d.id = t.department_id
                LEFT JOIN college c ON c.id = d.college_id
                WHERE t.id = ? AND t.status = 1
                """, id);
    }

    public Map<String, Object> findTeacherByUserId(Long userId) {
        return findOne("""
                SELECT t.id, t.user_id AS userId, t.department_id AS departmentId, t.teacher_no AS teacherNo,
                       t.teacher_name AS teacherName, t.gender, t.title, t.phone, t.email,
                       d.department_name AS departmentName, c.college_name AS collegeName
                FROM teacher t
                LEFT JOIN department d ON d.id = t.department_id
                LEFT JOIN college c ON c.id = d.college_id
                WHERE t.user_id = ? AND t.status = 1
                """, userId);
    }

    public int insertTeacher(Map<String, Object> teacher) {
        return jdbcTemplate.update("""
                INSERT INTO teacher(user_id, department_id, teacher_no, teacher_name, gender, title, phone, email, status)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, 1)
                """, teacher.get("userId"), teacher.get("departmentId"), teacher.get("teacherNo"),
                teacher.get("teacherName"), teacher.get("gender"), teacher.get("title"),
                teacher.get("phone"), teacher.get("email"));
    }

    public int updateTeacher(Long id, Map<String, Object> teacher) {
        return jdbcTemplate.update("""
                UPDATE teacher
                SET user_id = ?, department_id = ?, teacher_no = ?, teacher_name = ?, gender = ?,
                    title = ?, phone = ?, email = ?
                WHERE id = ? AND status = 1
                """, teacher.get("userId"), teacher.get("departmentId"), teacher.get("teacherNo"),
                teacher.get("teacherName"), teacher.get("gender"), teacher.get("title"),
                teacher.get("phone"), teacher.get("email"), id);
    }

    public int disableTeacher(Long id) {
        return jdbcTemplate.update("UPDATE teacher SET status = 0 WHERE id = ?", id);
    }

    public int countTeacherNo(String teacherNo, Long excludeId) {
        if (excludeId == null) {
            return count("SELECT COUNT(*) FROM teacher WHERE teacher_no = ?", teacherNo);
        }
        return count("SELECT COUNT(*) FROM teacher WHERE teacher_no = ? AND id <> ?", teacherNo, excludeId);
    }

    public List<Map<String, Object>> listTeachingTasks(Long teacherId) {
        if (teacherId == null) {
            return jdbcTemplate.queryForList(teachingTaskSelect() + " WHERE tt.task_status <> 0 ORDER BY tt.id DESC");
        }
        return jdbcTemplate.queryForList(teachingTaskSelect() + " WHERE tt.teacher_id = ? AND tt.task_status <> 0 ORDER BY tt.id DESC", teacherId);
    }

    public Map<String, Object> findTeachingTaskById(Long id) {
        return findOne(teachingTaskSelect() + " WHERE tt.id = ? AND tt.task_status <> 0", id);
    }

    public int insertTeachingTask(Map<String, Object> task) {
        return jdbcTemplate.update("""
                INSERT INTO teaching_task(course_id, teacher_id, semester, weekday, start_section,
                                          end_section, weeks, classroom, capacity, selected_count, task_status)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, 0, 1)
                """, task.get("courseId"), task.get("teacherId"), task.get("semester"), task.get("weekday"),
                task.get("startSection"), task.get("endSection"), task.get("weeks"), task.get("classroom"),
                defaultInt(task.get("capacity"), 60));
    }

    public int updateTeachingTask(Long id, Map<String, Object> task) {
        return jdbcTemplate.update("""
                UPDATE teaching_task
                SET course_id = ?, teacher_id = ?, semester = ?, weekday = ?, start_section = ?,
                    end_section = ?, weeks = ?, classroom = ?, capacity = ?, task_status = ?
                WHERE id = ? AND task_status <> 0
                """, task.get("courseId"), task.get("teacherId"), task.get("semester"), task.get("weekday"),
                task.get("startSection"), task.get("endSection"), task.get("weeks"), task.get("classroom"),
                defaultInt(task.get("capacity"), 60), defaultInt(task.get("taskStatus"), 1), id);
    }

    public int disableTeachingTask(Long id) {
        return jdbcTemplate.update("UPDATE teaching_task SET task_status = 0 WHERE id = ?", id);
    }

    public int countTeachingConflict(Map<String, Object> task, Long excludeId) {
        String condition = excludeId == null ? "" : " AND id <> ?";
        Object[] args = excludeId == null
                ? new Object[]{task.get("teacherId"), task.get("semester"), task.get("weekday"), task.get("startSection"), task.get("endSection")}
                : new Object[]{task.get("teacherId"), task.get("semester"), task.get("weekday"), task.get("startSection"), task.get("endSection"), excludeId};
        return count("""
                SELECT COUNT(*)
                FROM teaching_task
                WHERE teacher_id = ? AND semester = ? AND weekday = ? AND task_status <> 0
                  AND start_section <= ? AND end_section >= ?
                """ + condition, args);
    }

    public int countStudentCourseTimeConflict(Long studentId, String semester, Object weekday, Object startSection, Object endSection) {
        return count("""
                SELECT COUNT(*)
                FROM student_course sc
                JOIN teaching_task tt ON tt.id = sc.teaching_task_id
                WHERE sc.student_id = ? AND sc.status = 1
                  AND tt.task_status <> 0 AND tt.semester = ? AND tt.weekday = ?
                  AND tt.start_section <= ? AND tt.end_section >= ?
                """, studentId, semester, weekday, endSection, startSection);
    }

    public List<Map<String, Object>> listStudentCourses(Long studentId, Long teacherId) {
        String base = """
                SELECT sc.id, sc.student_id AS studentId, sc.teaching_task_id AS teachingTaskId,
                       sc.select_time AS selectTime, sc.score, sc.grade_status AS gradeStatus,
                       sc.status, sc.remark, s.student_no AS studentNo, s.student_name AS studentName,
                       c.course_code AS courseCode, c.course_name AS courseName, c.credit,
                       t.teacher_name AS teacherName, tt.semester, tt.weekday,
                       tt.start_section AS startSection, tt.end_section AS endSection, tt.classroom
                FROM student_course sc
                JOIN student s ON s.id = sc.student_id
                JOIN teaching_task tt ON tt.id = sc.teaching_task_id
                JOIN course c ON c.id = tt.course_id
                JOIN teacher t ON t.id = tt.teacher_id
                WHERE sc.status = 1
                """;
        if (studentId != null) {
            return jdbcTemplate.queryForList(base + " AND sc.student_id = ? ORDER BY sc.id DESC", studentId);
        }
        if (teacherId != null) {
            return jdbcTemplate.queryForList(base + " AND tt.teacher_id = ? ORDER BY sc.id DESC", teacherId);
        }
        return jdbcTemplate.queryForList(base + " ORDER BY sc.id DESC");
    }

    public List<Map<String, Object>> listSelectableTasks(Long studentId, String semester) {
        return jdbcTemplate.queryForList("""
                SELECT tt.id, tt.course_id AS courseId, tt.teacher_id AS teacherId, tt.semester,
                       tt.weekday, tt.start_section AS startSection, tt.end_section AS endSection,
                       tt.weeks, tt.classroom, tt.capacity, tt.selected_count AS selectedCount,
                       c.course_code AS courseCode, c.course_name AS courseName, c.credit,
                       t.teacher_name AS teacherName,
                       CASE WHEN sc.id IS NULL THEN 0 ELSE 1 END AS selectedByMe,
                       sc.id AS studentCourseId
                FROM teaching_task tt
                JOIN course c ON c.id = tt.course_id
                JOIN teacher t ON t.id = tt.teacher_id
                LEFT JOIN student_course sc
                  ON sc.teaching_task_id = tt.id AND sc.student_id = ? AND sc.status = 1
                WHERE tt.task_status = 1 AND tt.semester = ?
                ORDER BY tt.id DESC
                """, studentId, semester);
    }

    public int insertStudentCourse(Long studentId, Long teachingTaskId) {
        return jdbcTemplate.update("""
                INSERT INTO student_course(student_id, teaching_task_id, grade_status, status)
                VALUES (?, ?, 0, 1)
                """, studentId, teachingTaskId);
    }

    public int reactivateStudentCourse(Long id) {
        return jdbcTemplate.update("""
                UPDATE student_course
                SET status = 1, select_time = NOW()
                WHERE id = ?
                """, id);
    }

    public int disableStudentCourse(Long id) {
        return jdbcTemplate.update("UPDATE student_course SET status = 0 WHERE id = ? AND status = 1", id);
    }

    public int updateSelectedCount(Long teachingTaskId, int delta) {
        return jdbcTemplate.update("""
                UPDATE teaching_task
                SET selected_count = selected_count + ?
                WHERE id = ? AND selected_count + ? >= 0
                """, delta, teachingTaskId, delta);
    }

    public Map<String, Object> findStudentCourseByStudentAndTask(Long studentId, Long teachingTaskId) {
        return findOne("""
                SELECT id, status
                FROM student_course
                WHERE student_id = ? AND teaching_task_id = ?
                """, studentId, teachingTaskId);
    }

    public Map<String, Object> findStudentCourseById(Long id) {
        return findOne("""
                SELECT sc.id, sc.student_id AS studentId, sc.teaching_task_id AS teachingTaskId,
                       sc.status, tt.selected_count AS selectedCount
                FROM student_course sc
                JOIN teaching_task tt ON tt.id = sc.teaching_task_id
                WHERE sc.id = ?
                """, id);
    }

    public int updateScore(Long id, BigDecimal score, String remark) {
        return jdbcTemplate.update("""
                UPDATE student_course
                SET score = ?, grade_status = 1, remark = ?
                WHERE id = ? AND status = 1
                """, score, remark, id);
    }

    public List<Map<String, Object>> listTeacherStudents() {
        return jdbcTemplate.queryForList("""
                SELECT ts.id, ts.teacher_id AS teacherId, ts.student_id AS studentId, ts.guide_type AS guideType,
                       ts.start_date AS startDate, ts.end_date AS endDate, ts.status,
                       t.teacher_name AS teacherName, s.student_no AS studentNo, s.student_name AS studentName
                FROM teacher_student ts
                JOIN teacher t ON t.id = ts.teacher_id
                JOIN student s ON s.id = ts.student_id
                WHERE ts.status = 1
                ORDER BY ts.id DESC
                """);
    }

    public int insertTeacherStudent(Map<String, Object> relation) {
        return jdbcTemplate.update("""
                INSERT INTO teacher_student(teacher_id, student_id, guide_type, start_date, end_date, status)
                VALUES (?, ?, ?, ?, ?, 1)
                """, relation.get("teacherId"), relation.get("studentId"), relation.get("guideType"),
                relation.get("startDate"), relation.get("endDate"));
    }

    public int updateTeacherStudent(Long id, Map<String, Object> relation) {
        return jdbcTemplate.update("""
                UPDATE teacher_student
                SET teacher_id = ?, student_id = ?, guide_type = ?, start_date = ?, end_date = ?
                WHERE id = ? AND status = 1
                """, relation.get("teacherId"), relation.get("studentId"), relation.get("guideType"),
                relation.get("startDate"), relation.get("endDate"), id);
    }

    public int disableTeacherStudent(Long id) {
        return jdbcTemplate.update("UPDATE teacher_student SET status = 0 WHERE id = ?", id);
    }

    public Map<String, Object> overviewReport() {
        return Map.of(
                "studentCount", count("SELECT COUNT(*) FROM student WHERE status = 1"),
                "teacherCount", count("SELECT COUNT(*) FROM teacher WHERE status = 1"),
                "courseCount", count("SELECT COUNT(*) FROM course WHERE status = 1"),
                "teachingTaskCount", count("SELECT COUNT(*) FROM teaching_task WHERE task_status <> 0"),
                "selectionCount", count("SELECT COUNT(*) FROM student_course WHERE status = 1")
        );
    }

    public List<Map<String, Object>> collegeStudentReport() {
        return jdbcTemplate.queryForList("""
                SELECT c.college_name AS name, COUNT(s.id) AS value
                FROM college c
                LEFT JOIN major m ON m.college_id = c.id AND m.status = 1
                LEFT JOIN class_info ci ON ci.major_id = m.id AND ci.status = 1
                LEFT JOIN student s ON s.class_id = ci.id AND s.status = 1
                WHERE c.status = 1
                GROUP BY c.id, c.college_name
                ORDER BY value DESC
                """);
    }

    public List<Map<String, Object>> gradeDistributionReport() {
        return jdbcTemplate.queryForList("""
                SELECT CASE
                         WHEN score >= 90 THEN '优秀'
                         WHEN score >= 80 THEN '良好'
                         WHEN score >= 70 THEN '中等'
                         WHEN score >= 60 THEN '及格'
                         ELSE '不及格'
                       END AS name,
                       COUNT(*) AS value
                FROM student_course
                WHERE status = 1 AND grade_status = 1 AND score IS NOT NULL
                GROUP BY name
                ORDER BY MIN(score) DESC
                """);
    }

    public List<Map<String, Object>> teachingLoadReport() {
        return jdbcTemplate.queryForList("""
                SELECT t.teacher_name AS teacherName, COUNT(tt.id) AS taskCount,
                       COALESCE(SUM(tt.selected_count), 0) AS selectedCount
                FROM teacher t
                LEFT JOIN teaching_task tt ON tt.teacher_id = t.id AND tt.task_status <> 0
                WHERE t.status = 1
                GROUP BY t.id, t.teacher_name
                ORDER BY taskCount DESC, selectedCount DESC
                """);
    }

    private String teachingTaskSelect() {
        return """
                SELECT tt.id, tt.course_id AS courseId, tt.teacher_id AS teacherId, tt.semester,
                       tt.weekday, tt.start_section AS startSection, tt.end_section AS endSection,
                       tt.weeks, tt.classroom, tt.capacity, tt.selected_count AS selectedCount,
                       tt.task_status AS taskStatus, c.course_code AS courseCode, c.course_name AS courseName,
                       c.credit, t.teacher_no AS teacherNo, t.teacher_name AS teacherName
                FROM teaching_task tt
                JOIN course c ON c.id = tt.course_id
                JOIN teacher t ON t.id = tt.teacher_id
                """;
    }

    private Map<String, Object> findOne(String sql, Object... args) {
        try {
            return jdbcTemplate.queryForMap(sql, args);
        } catch (EmptyResultDataAccessException exception) {
            return null;
        }
    }

    private int count(String sql, Object... args) {
        Integer value = jdbcTemplate.queryForObject(sql, Integer.class, args);
        return value == null ? 0 : value;
    }

    private int defaultInt(Object value, int defaultValue) {
        return value == null ? defaultValue : Integer.parseInt(value.toString());
    }
}
