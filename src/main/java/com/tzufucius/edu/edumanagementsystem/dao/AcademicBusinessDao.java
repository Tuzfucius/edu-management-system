package com.tzufucius.edu.edumanagementsystem.dao;

import com.tzufucius.edu.edumanagementsystem.dto.request.StudentRequest;
import com.tzufucius.edu.edumanagementsystem.dto.request.SysUserRequest;
import com.tzufucius.edu.edumanagementsystem.dto.request.TeacherRequest;
import com.tzufucius.edu.edumanagementsystem.dto.request.TeacherStudentRequest;
import com.tzufucius.edu.edumanagementsystem.dto.request.TeachingTaskRequest;
import com.tzufucius.edu.edumanagementsystem.dto.vo.NameValueReportVO;
import com.tzufucius.edu.edumanagementsystem.dto.vo.OverviewReportVO;
import com.tzufucius.edu.edumanagementsystem.dto.vo.SelectableTaskVO;
import com.tzufucius.edu.edumanagementsystem.dto.vo.StudentCourseVO;
import com.tzufucius.edu.edumanagementsystem.dto.vo.StudentVO;
import com.tzufucius.edu.edumanagementsystem.dto.vo.SysUserVO;
import com.tzufucius.edu.edumanagementsystem.dto.vo.TeacherStudentVO;
import com.tzufucius.edu.edumanagementsystem.dto.vo.TeacherVO;
import com.tzufucius.edu.edumanagementsystem.dto.vo.TeachingLoadReportVO;
import com.tzufucius.edu.edumanagementsystem.dto.vo.TeachingTaskVO;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class AcademicBusinessDao {

    private final JdbcTemplate jdbcTemplate;

    public AcademicBusinessDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public LoginUserRecord findUserForLogin(String username, String role) {
        String sql = """
                SELECT u.id, u.username, u.password, u.role, u.status,
                       COALESCE(t.teacher_name, s.student_name, u.username) AS displayName
                FROM sys_user u
                LEFT JOIN teacher t ON t.user_id = u.id AND t.status = 1
                LEFT JOIN student s ON s.user_id = u.id AND s.status = 1
                WHERE u.username = ? AND u.role = ? AND u.status = 1
                """;
        return findOne(sql, (rs, rowNum) -> new LoginUserRecord(
                rs.getLong("id"),
                rs.getString("username"),
                rs.getString("password"),
                rs.getString("role"),
                rs.getString("displayName")
        ), username, role);
    }

    public void updateLastLogin(Long id) {
        jdbcTemplate.update("UPDATE sys_user SET last_login_at = NOW() WHERE id = ?", id);
    }

    public List<SysUserVO> listUsers() {
        return jdbcTemplate.query("""
                SELECT id, username, role, status, last_login_at AS lastLoginAt,
                       created_at AS createdAt, updated_at AS updatedAt
                FROM sys_user
                ORDER BY id DESC
                """, this::sysUserVO);
    }

    public SysUserVO findUserById(Long id) {
        return findOne("""
                SELECT id, username, role, status, last_login_at AS lastLoginAt,
                       created_at AS createdAt, updated_at AS updatedAt
                FROM sys_user
                WHERE id = ?
                """, this::sysUserVO, id);
    }

    public Long insertUser(SysUserRequest user) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        PreparedStatementCreator creator = connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement("""
                    INSERT INTO sys_user(username, password, role, status)
                    VALUES (?, ?, ?, COALESCE(?, 1))
                    """, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setObject(1, user.username());
            preparedStatement.setObject(2, user.password());
            preparedStatement.setObject(3, user.role());
            preparedStatement.setObject(4, user.status());
            return preparedStatement;
        };
        jdbcTemplate.update(creator, keyHolder);
        Number generatedKey = keyHolder.getKey();
        return generatedKey == null ? null : generatedKey.longValue();
    }

    public int updateUser(Long id, SysUserRequest user) {
        if (user.password() == null || user.password().isBlank()) {
            return jdbcTemplate.update("""
                    UPDATE sys_user
                    SET username = ?, role = ?, status = COALESCE(?, status)
                    WHERE id = ?
                    """, user.username(), user.role(), user.status(), id);
        }
        return jdbcTemplate.update("""
                UPDATE sys_user
                SET username = ?, password = ?, role = ?, status = COALESCE(?, status)
                WHERE id = ?
                """, user.username(), user.password(), user.role(), user.status(), id);
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

    public List<StudentVO> listStudents() {
        return jdbcTemplate.query(studentSelect() + " WHERE s.status = 1 ORDER BY s.id DESC", this::studentVO);
    }

    public StudentVO findStudentById(Long id) {
        return findOne(studentSelect() + " WHERE s.id = ? AND s.status = 1", this::studentVO, id);
    }

    public StudentVO findStudentByUserId(Long userId) {
        return findOne(studentSelect() + " WHERE s.user_id = ? AND s.status = 1", this::studentVO, userId);
    }

    public int insertStudent(Long userId, StudentRequest student) {
        return jdbcTemplate.update("""
                INSERT INTO student(user_id, class_id, student_no, student_name, gender, birth_date,
                                    phone, email, enrollment_year, status)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, 1)
                """, userId, student.classId(), student.studentNo(), student.studentName(), student.gender(),
                student.birthDate(), student.phone(), student.email(), student.enrollmentYear());
    }

    public int updateStudent(Long id, Long userId, StudentRequest student) {
        return jdbcTemplate.update("""
                UPDATE student
                SET user_id = ?, class_id = ?, student_no = ?, student_name = ?, gender = ?,
                    birth_date = ?, phone = ?, email = ?, enrollment_year = ?
                WHERE id = ? AND status = 1
                """, userId, student.classId(), student.studentNo(), student.studentName(), student.gender(),
                student.birthDate(), student.phone(), student.email(), student.enrollmentYear(), id);
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

    public List<TeacherVO> listTeachers() {
        return jdbcTemplate.query(teacherSelect() + " WHERE t.status = 1 ORDER BY t.id DESC", this::teacherVO);
    }

    public TeacherVO findTeacherById(Long id) {
        return findOne(teacherSelect() + " WHERE t.id = ? AND t.status = 1", this::teacherVO, id);
    }

    public TeacherVO findTeacherByUserId(Long userId) {
        return findOne(teacherSelect() + " WHERE t.user_id = ? AND t.status = 1", this::teacherVO, userId);
    }

    public int insertTeacher(Long userId, TeacherRequest teacher) {
        return jdbcTemplate.update("""
                INSERT INTO teacher(user_id, department_id, teacher_no, teacher_name, gender, title, phone, email, status)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, 1)
                """, userId, teacher.departmentId(), teacher.teacherNo(), teacher.teacherName(), teacher.gender(),
                teacher.title(), teacher.phone(), teacher.email());
    }

    public int updateTeacher(Long id, Long userId, TeacherRequest teacher) {
        return jdbcTemplate.update("""
                UPDATE teacher
                SET user_id = ?, department_id = ?, teacher_no = ?, teacher_name = ?, gender = ?,
                    title = ?, phone = ?, email = ?
                WHERE id = ? AND status = 1
                """, userId, teacher.departmentId(), teacher.teacherNo(), teacher.teacherName(), teacher.gender(),
                teacher.title(), teacher.phone(), teacher.email(), id);
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

    public List<TeachingTaskVO> listTeachingTasks(Long teacherId) {
        if (teacherId == null) {
            return jdbcTemplate.query(teachingTaskSelect() + " WHERE tt.task_status <> 0 ORDER BY tt.id DESC", this::teachingTaskVO);
        }
        return jdbcTemplate.query(teachingTaskSelect() + " WHERE tt.teacher_id = ? AND tt.task_status <> 0 ORDER BY tt.id DESC",
                this::teachingTaskVO, teacherId);
    }

    public TeachingTaskVO findTeachingTaskById(Long id) {
        return findOne(teachingTaskSelect() + " WHERE tt.id = ? AND tt.task_status <> 0", this::teachingTaskVO, id);
    }

    public int insertTeachingTask(TeachingTaskRequest task) {
        return jdbcTemplate.update("""
                INSERT INTO teaching_task(course_id, teacher_id, semester, weekday, start_section,
                                          end_section, weeks, classroom, capacity, selected_count, task_status)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, 0, 1)
                """, task.courseId(), task.teacherId(), task.semester(), task.weekday(), task.startSection(),
                task.endSection(), task.weeks(), task.classroom(), defaultInt(task.capacity(), 60));
    }

    public int countEnabledCourseById(Long courseId) {
        return count("SELECT COUNT(*) FROM course WHERE id = ? AND status = 1", courseId);
    }

    public int updateTeachingTask(Long id, TeachingTaskRequest task) {
        return jdbcTemplate.update("""
                UPDATE teaching_task
                SET course_id = ?, teacher_id = ?, semester = ?, weekday = ?, start_section = ?,
                    end_section = ?, weeks = ?, classroom = ?, capacity = ?, task_status = ?
                WHERE id = ? AND task_status <> 0
                """, task.courseId(), task.teacherId(), task.semester(), task.weekday(), task.startSection(),
                task.endSection(), task.weeks(), task.classroom(), defaultInt(task.capacity(), 60),
                defaultInt(task.taskStatus(), 1), id);
    }

    public int disableTeachingTask(Long id) {
        return jdbcTemplate.update("UPDATE teaching_task SET task_status = 0 WHERE id = ?", id);
    }

    public int countTeachingConflict(TeachingTaskRequest task, Long excludeId) {
        String condition = excludeId == null ? "" : " AND id <> ?";
        Object[] args = excludeId == null
                ? new Object[]{task.teacherId(), task.semester(), task.weekday(), task.startSection(), task.endSection()}
                : new Object[]{task.teacherId(), task.semester(), task.weekday(), task.startSection(), task.endSection(), excludeId};
        return count("""
                SELECT COUNT(*)
                FROM teaching_task
                WHERE teacher_id = ? AND semester = ? AND weekday = ? AND task_status <> 0
                  AND start_section <= ? AND end_section >= ?
                """ + condition, args);
    }

    public int countStudentCourseTimeConflict(Long studentId, String semester, Integer weekday, Integer startSection, Integer endSection) {
        return count("""
                SELECT COUNT(*)
                FROM student_course sc
                JOIN teaching_task tt ON tt.id = sc.teaching_task_id
                WHERE sc.student_id = ? AND sc.status = 1
                  AND tt.task_status <> 0 AND tt.semester = ? AND tt.weekday = ?
                  AND tt.start_section <= ? AND tt.end_section >= ?
                """, studentId, semester, weekday, endSection, startSection);
    }

    public List<StudentCourseVO> listStudentCourses(Long studentId, Long teacherId) {
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
            return jdbcTemplate.query(base + " AND sc.student_id = ? ORDER BY sc.id DESC", this::studentCourseVO, studentId);
        }
        if (teacherId != null) {
            return jdbcTemplate.query(base + " AND tt.teacher_id = ? ORDER BY sc.id DESC", this::studentCourseVO, teacherId);
        }
        return jdbcTemplate.query(base + " ORDER BY sc.id DESC", this::studentCourseVO);
    }

    public List<SelectableTaskVO> listSelectableTasks(Long studentId, String semester) {
        return jdbcTemplate.query("""
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
                WHERE tt.task_status = 1 AND c.status = 1 AND tt.semester = ?
                ORDER BY tt.id DESC
                """, this::selectableTaskVO, studentId, semester);
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

    public StudentCourseStatus findStudentCourseByStudentAndTask(Long studentId, Long teachingTaskId) {
        return findOne("""
                SELECT id, status
                FROM student_course
                WHERE student_id = ? AND teaching_task_id = ?
                """, (rs, rowNum) -> new StudentCourseStatus(rs.getLong("id"), rs.getInt("status")),
                studentId, teachingTaskId);
    }

    public StudentCourseDropRecord findStudentCourseById(Long id) {
        return findOne("""
                SELECT sc.id, sc.student_id AS studentId, sc.teaching_task_id AS teachingTaskId,
                       sc.status, sc.grade_status AS gradeStatus, sc.score, tt.selected_count AS selectedCount
                FROM student_course sc
                JOIN teaching_task tt ON tt.id = sc.teaching_task_id
                WHERE sc.id = ?
                """, (rs, rowNum) -> new StudentCourseDropRecord(
                        rs.getLong("id"),
                        rs.getLong("studentId"),
                        rs.getLong("teachingTaskId"),
                        rs.getInt("status"),
                        rs.getInt("gradeStatus"),
                        rs.getBigDecimal("score"),
                        rs.getInt("selectedCount")
                ), id);
    }

    public int updateScore(Long id, BigDecimal score, String remark) {
        return jdbcTemplate.update("""
                UPDATE student_course
                SET score = ?, grade_status = 1, remark = ?
                WHERE id = ? AND status = 1
                """, score, remark, id);
    }

    public int revokeScore(Long id) {
        return jdbcTemplate.update("""
                UPDATE student_course
                SET score = NULL, grade_status = 0, remark = NULL
                WHERE id = ? AND status = 1
                """, id);
    }

    public List<TeacherStudentVO> listTeacherStudents() {
        return jdbcTemplate.query("""
                SELECT ts.id, ts.teacher_id AS teacherId, ts.student_id AS studentId, ts.guide_type AS guideType,
                       ts.start_date AS startDate, ts.end_date AS endDate, ts.status,
                       t.teacher_name AS teacherName, s.student_no AS studentNo, s.student_name AS studentName
                FROM teacher_student ts
                JOIN teacher t ON t.id = ts.teacher_id
                JOIN student s ON s.id = ts.student_id
                WHERE ts.status = 1
                ORDER BY ts.id DESC
                """, this::teacherStudentVO);
    }

    public int insertTeacherStudent(TeacherStudentRequest relation) {
        return jdbcTemplate.update("""
                INSERT INTO teacher_student(teacher_id, student_id, guide_type, start_date, end_date, status)
                VALUES (?, ?, ?, ?, ?, 1)
                """, relation.teacherId(), relation.studentId(), relation.guideType(),
                relation.startDate(), relation.endDate());
    }

    public int updateTeacherStudent(Long id, TeacherStudentRequest relation) {
        return jdbcTemplate.update("""
                UPDATE teacher_student
                SET teacher_id = ?, student_id = ?, guide_type = ?, start_date = ?, end_date = ?
                WHERE id = ? AND status = 1
                """, relation.teacherId(), relation.studentId(), relation.guideType(),
                relation.startDate(), relation.endDate(), id);
    }

    public int disableTeacherStudent(Long id) {
        return jdbcTemplate.update("UPDATE teacher_student SET status = 0 WHERE id = ?", id);
    }

    public OverviewReportVO overviewReport() {
        return new OverviewReportVO(
                count("SELECT COUNT(*) FROM student WHERE status = 1"),
                count("SELECT COUNT(*) FROM teacher WHERE status = 1"),
                count("SELECT COUNT(*) FROM course WHERE status = 1"),
                count("SELECT COUNT(*) FROM teaching_task WHERE task_status <> 0"),
                count("SELECT COUNT(*) FROM student_course WHERE status = 1")
        );
    }

    public List<NameValueReportVO> collegeStudentReport() {
        return jdbcTemplate.query("""
                SELECT c.college_name AS name, COUNT(s.id) AS value
                FROM college c
                LEFT JOIN major m ON m.college_id = c.id AND m.status = 1
                LEFT JOIN class_info ci ON ci.major_id = m.id AND ci.status = 1
                LEFT JOIN student s ON s.class_id = ci.id AND s.status = 1
                WHERE c.status = 1
                GROUP BY c.id, c.college_name
                ORDER BY value DESC
                """, this::nameValueReportVO);
    }

    public List<NameValueReportVO> gradeDistributionReport() {
        return jdbcTemplate.query("""
                SELECT CASE
                         WHEN score >= 90 THEN 'excellent'
                         WHEN score >= 80 THEN 'good'
                         WHEN score >= 70 THEN 'medium'
                         WHEN score >= 60 THEN 'pass'
                         ELSE 'fail'
                       END AS name,
                       COUNT(*) AS value
                FROM student_course
                WHERE status = 1 AND grade_status = 1 AND score IS NOT NULL
                GROUP BY name
                ORDER BY MIN(score) DESC
                """, this::nameValueReportVO);
    }

    public List<TeachingLoadReportVO> teachingLoadReport() {
        return jdbcTemplate.query("""
                SELECT t.teacher_name AS teacherName, COUNT(tt.id) AS taskCount,
                       COALESCE(SUM(tt.selected_count), 0) AS selectedCount
                FROM teacher t
                LEFT JOIN teaching_task tt ON tt.teacher_id = t.id AND tt.task_status <> 0
                WHERE t.status = 1
                GROUP BY t.id, t.teacher_name
                ORDER BY taskCount DESC, selectedCount DESC
                """, (rs, rowNum) -> new TeachingLoadReportVO(
                        rs.getString("teacherName"),
                        rs.getInt("taskCount"),
                        rs.getInt("selectedCount")
                ));
    }

    private String studentSelect() {
        return """
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
                """;
    }

    private String teacherSelect() {
        return """
                SELECT t.id, t.user_id AS userId, t.department_id AS departmentId, t.teacher_no AS teacherNo,
                       t.teacher_name AS teacherName, t.gender, t.title, t.phone, t.email, t.status,
                       d.department_name AS departmentName, c.college_name AS collegeName, u.username
                FROM teacher t
                LEFT JOIN sys_user u ON u.id = t.user_id
                LEFT JOIN department d ON d.id = t.department_id
                LEFT JOIN college c ON c.id = d.college_id
                """;
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

    private SysUserVO sysUserVO(ResultSet rs, int rowNum) throws SQLException {
        return new SysUserVO(rs.getLong("id"), rs.getString("username"), rs.getString("role"),
                rs.getInt("status"), localDateTime(rs.getTimestamp("lastLoginAt")),
                localDateTime(rs.getTimestamp("createdAt")), localDateTime(rs.getTimestamp("updatedAt")));
    }

    private StudentVO studentVO(ResultSet rs, int rowNum) throws SQLException {
        return new StudentVO(rs.getLong("id"), nullableLong(rs, "userId"), rs.getLong("classId"),
                rs.getString("studentNo"), rs.getString("studentName"), rs.getString("gender"),
                localDate(rs.getDate("birthDate")), rs.getString("phone"), rs.getString("email"),
                rs.getInt("enrollmentYear"), rs.getInt("status"), rs.getString("className"),
                rs.getString("majorName"), rs.getString("collegeName"), rs.getString("username"));
    }

    private TeacherVO teacherVO(ResultSet rs, int rowNum) throws SQLException {
        return new TeacherVO(rs.getLong("id"), nullableLong(rs, "userId"), rs.getLong("departmentId"),
                rs.getString("teacherNo"), rs.getString("teacherName"), rs.getString("gender"),
                rs.getString("title"), rs.getString("phone"), rs.getString("email"), rs.getInt("status"),
                rs.getString("departmentName"), rs.getString("collegeName"), rs.getString("username"));
    }

    private TeachingTaskVO teachingTaskVO(ResultSet rs, int rowNum) throws SQLException {
        return new TeachingTaskVO(rs.getLong("id"), rs.getLong("courseId"), rs.getLong("teacherId"),
                rs.getString("semester"), rs.getInt("weekday"), rs.getInt("startSection"),
                rs.getInt("endSection"), rs.getString("weeks"), rs.getString("classroom"),
                rs.getInt("capacity"), rs.getInt("selectedCount"), rs.getInt("taskStatus"),
                rs.getString("courseCode"), rs.getString("courseName"), rs.getBigDecimal("credit"),
                rs.getString("teacherNo"), rs.getString("teacherName"));
    }

    private StudentCourseVO studentCourseVO(ResultSet rs, int rowNum) throws SQLException {
        return new StudentCourseVO(rs.getLong("id"), rs.getLong("studentId"), rs.getLong("teachingTaskId"),
                localDateTime(rs.getTimestamp("selectTime")), rs.getBigDecimal("score"), rs.getInt("gradeStatus"),
                rs.getInt("status"), rs.getString("remark"), rs.getString("studentNo"), rs.getString("studentName"),
                rs.getString("courseCode"), rs.getString("courseName"), rs.getBigDecimal("credit"),
                rs.getString("teacherName"), rs.getString("semester"), rs.getInt("weekday"),
                rs.getInt("startSection"), rs.getInt("endSection"), rs.getString("classroom"));
    }

    private SelectableTaskVO selectableTaskVO(ResultSet rs, int rowNum) throws SQLException {
        return new SelectableTaskVO(rs.getLong("id"), rs.getLong("courseId"), rs.getLong("teacherId"),
                rs.getString("semester"), rs.getInt("weekday"), rs.getInt("startSection"),
                rs.getInt("endSection"), rs.getString("weeks"), rs.getString("classroom"),
                rs.getInt("capacity"), rs.getInt("selectedCount"), rs.getString("courseCode"),
                rs.getString("courseName"), rs.getBigDecimal("credit"), rs.getString("teacherName"),
                rs.getInt("selectedByMe"), nullableLong(rs, "studentCourseId"));
    }

    private TeacherStudentVO teacherStudentVO(ResultSet rs, int rowNum) throws SQLException {
        return new TeacherStudentVO(rs.getLong("id"), rs.getLong("teacherId"), rs.getLong("studentId"),
                rs.getString("guideType"), localDate(rs.getDate("startDate")), localDate(rs.getDate("endDate")),
                rs.getInt("status"), rs.getString("teacherName"), rs.getString("studentNo"), rs.getString("studentName"));
    }

    private NameValueReportVO nameValueReportVO(ResultSet rs, int rowNum) throws SQLException {
        return new NameValueReportVO(rs.getString("name"), rs.getInt("value"));
    }

    private <T> T findOne(String sql, org.springframework.jdbc.core.RowMapper<T> mapper, Object... args) {
        try {
            return jdbcTemplate.queryForObject(sql, mapper, args);
        } catch (EmptyResultDataAccessException exception) {
            return null;
        }
    }

    private int count(String sql, Object... args) {
        Integer value = jdbcTemplate.queryForObject(sql, Integer.class, args);
        return value == null ? 0 : value;
    }

    private int defaultInt(Integer value, int defaultValue) {
        return value == null ? defaultValue : value;
    }

    private Long nullableLong(ResultSet rs, String column) throws SQLException {
        long value = rs.getLong(column);
        return rs.wasNull() ? null : value;
    }

    private LocalDate localDate(Date date) {
        return date == null ? null : date.toLocalDate();
    }

    private LocalDateTime localDateTime(Timestamp timestamp) {
        return timestamp == null ? null : timestamp.toLocalDateTime();
    }

    public record LoginUserRecord(Long id, String username, String password, String role, String displayName) {
    }

    public record StudentCourseStatus(Long id, Integer status) {
    }

    public record StudentCourseDropRecord(Long id, Long studentId, Long teachingTaskId, Integer status,
                                          Integer gradeStatus, BigDecimal score, Integer selectedCount) {
    }
}
