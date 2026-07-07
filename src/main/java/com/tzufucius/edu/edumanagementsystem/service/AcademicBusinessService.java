package com.tzufucius.edu.edumanagementsystem.service;

import com.tzufucius.edu.edumanagementsystem.auth.AuthContext;
import com.tzufucius.edu.edumanagementsystem.auth.AuthException;
import com.tzufucius.edu.edumanagementsystem.dao.AcademicBusinessDao;
import com.tzufucius.edu.edumanagementsystem.dto.request.ScoreUpdateRequest;
import com.tzufucius.edu.edumanagementsystem.dto.request.StudentCourseSelectRequest;
import com.tzufucius.edu.edumanagementsystem.dto.request.StudentRequest;
import com.tzufucius.edu.edumanagementsystem.dto.request.SysUserRequest;
import com.tzufucius.edu.edumanagementsystem.dto.request.TeacherRequest;
import com.tzufucius.edu.edumanagementsystem.dto.request.TeacherStudentRequest;
import com.tzufucius.edu.edumanagementsystem.dto.request.TeachingTaskRequest;
import com.tzufucius.edu.edumanagementsystem.dto.vo.LoginUserVO;
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
import com.tzufucius.edu.edumanagementsystem.exception.BusinessException;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class AcademicBusinessService {
    private final AcademicBusinessDao dao;

    public AcademicBusinessService(AcademicBusinessDao dao) {
        this.dao = dao;
    }

    public LoginUserVO login(String username, String password, String role) {
        validateLogin(username, password, role);
        try {
            AcademicBusinessDao.LoginUserRecord user = dao.findUserForLogin(username, role);
            if (user == null) {
                throw new RuntimeException("Password is incorrect");
            }
            if (!password.equals(user.password())) {
                throw new RuntimeException("Password is incorrect");
            }
            dao.updateLastLogin(user.id());
            return new LoginUserVO(user.id(), user.username(), user.displayName(), user.role());
        } catch (DataAccessException exception) {
            throw new RuntimeException("Login failed");
        }
    }

    public List<SysUserVO> listUsers() {
        return dao.listUsers();
    }

    public SysUserVO getUser(Long id) {
        return requireUser(id);
    }

    public Long createUser(SysUserRequest request) {
        return insertUser(normalizeUserRequest(request, request.role()));
    }

    public void updateUser(Long id, SysUserRequest request) {
        requiredId(id, "User id is required");
        validateUser(request, id);
        requireUser(id);
        if (dao.countUsername(request.username(), id) > 0) {
            throw new RuntimeException("Username already exists");
        }
        if (dao.updateUser(id, request) != 1) {
            throw new RuntimeException("Failed to update user");
        }
    }

    public void deleteUser(Long id) {
        requireUser(id);
        if (dao.disableUser(id) != 1) {
            throw new RuntimeException("Failed to disable user");
        }
    }

    public List<StudentVO> listStudents() {
        return dao.listStudents();
    }

    public StudentVO getStudent(Long id) {
        return requireStudent(id);
    }

    public StudentVO getStudentByUserId(Long userId) {
        StudentVO student = dao.findStudentByUserId(requiredId(userId, "User id is required"));
        if (student == null) {
            throw new RuntimeException("Current user is not bound to a student");
        }
        return student;
    }

    public StudentVO getStudentByUserId(Long userId, LoginUserVO currentUser) {
        requireOwnerOrAdmin(currentUser, requiredId(userId, "User id is required"));
        return getStudentByUserId(userId);
    }

    @Transactional
    public void createStudent(StudentRequest request) {
        Long userId = resolveUserId(request.userId(), request.account(), "STUDENT");
        validateStudent(request, userId);
        if (dao.countStudentNo(request.studentNo(), null) > 0) {
            throw new RuntimeException("Student number already exists");
        }
        if (dao.insertStudent(userId, request) != 1) {
            throw new RuntimeException("Failed to create student");
        }
    }

    @Transactional
    public void updateStudent(Long id, StudentRequest request) {
        requireStudent(id);
        Long userId = resolveUserId(request.userId(), request.account(), "STUDENT");
        validateStudent(request, userId);
        if (dao.countStudentNo(request.studentNo(), id) > 0) {
            throw new RuntimeException("Student number already exists");
        }
        if (dao.updateStudent(id, userId, request) != 1) {
            throw new RuntimeException("Failed to update student");
        }
    }

    public void deleteStudent(Long id) {
        requireStudent(id);
        if (dao.disableStudent(id) != 1) {
            throw new RuntimeException("Failed to disable student");
        }
    }

    public List<TeacherVO> listTeachers() {
        return dao.listTeachers();
    }

    public TeacherVO getTeacher(Long id) {
        return requireTeacher(id);
    }

    public TeacherVO getTeacherByUserId(Long userId) {
        TeacherVO teacher = dao.findTeacherByUserId(requiredId(userId, "User id is required"));
        if (teacher == null) {
            throw new RuntimeException("Current user is not bound to a teacher");
        }
        return teacher;
    }

    public TeacherVO getTeacherByUserId(Long userId, LoginUserVO currentUser) {
        requireOwnerOrAdmin(currentUser, requiredId(userId, "User id is required"));
        return getTeacherByUserId(userId);
    }

    @Transactional
    public void createTeacher(TeacherRequest request) {
        Long userId = resolveUserId(request.userId(), request.account(), "TEACHER");
        validateTeacher(request, userId);
        if (dao.countTeacherNo(request.teacherNo(), null) > 0) {
            throw new RuntimeException("Teacher number already exists");
        }
        if (dao.insertTeacher(userId, request) != 1) {
            throw new RuntimeException("Failed to create teacher");
        }
    }

    @Transactional
    public void updateTeacher(Long id, TeacherRequest request) {
        requireTeacher(id);
        Long userId = resolveUserId(request.userId(), request.account(), "TEACHER");
        validateTeacher(request, userId);
        if (dao.countTeacherNo(request.teacherNo(), id) > 0) {
            throw new RuntimeException("Teacher number already exists");
        }
        if (dao.updateTeacher(id, userId, request) != 1) {
            throw new RuntimeException("Failed to update teacher");
        }
    }

    public void deleteTeacher(Long id) {
        requireTeacher(id);
        if (dao.disableTeacher(id) != 1) {
            throw new RuntimeException("Failed to disable teacher");
        }
    }

    public List<TeachingTaskVO> listTeachingTasks(Long teacherId) {
        return dao.listTeachingTasks(teacherId);
    }

    public List<TeachingTaskVO> listTeachingTasks(Long teacherId, LoginUserVO currentUser) {
        if (AuthContext.hasAnyRole(currentUser, "ADMIN")) {
            return listTeachingTasks(teacherId);
        }
        AuthContext.requireAnyRole(currentUser, "TEACHER");
        Long currentTeacherId = currentTeacher(currentUser).id();
        if (teacherId != null && !teacherId.equals(currentTeacherId)) {
            throw new AuthException(403, "只能访问自己的任课安排");
        }
        return listTeachingTasks(currentTeacherId);
    }

    public TeachingTaskVO getTeachingTask(Long id) {
        return requireTeachingTask(id);
    }

    public TeachingTaskVO getTeachingTask(Long id, LoginUserVO currentUser) {
        TeachingTaskVO task = requireTeachingTask(id);
        if (AuthContext.hasAnyRole(currentUser, "ADMIN")) {
            return task;
        }
        AuthContext.requireAnyRole(currentUser, "TEACHER");
        if (!task.teacherId().equals(currentTeacher(currentUser).id())) {
            throw new AuthException(403, "只能访问自己的任课安排");
        }
        return task;
    }

    public void createTeachingTask(TeachingTaskRequest request) {
        validateTeachingTask(request);
        if (dao.countTeachingConflict(request, null) > 0) {
            throw new RuntimeException("Teacher has a time conflict");
        }
        if (dao.insertTeachingTask(request) != 1) {
            throw new RuntimeException("Failed to create teaching task");
        }
    }

    public void updateTeachingTask(Long id, TeachingTaskRequest request) {
        requireTeachingTask(id);
        validateTeachingTask(request);
        if (dao.countTeachingConflict(request, id) > 0) {
            throw new RuntimeException("Teacher has a time conflict");
        }
        if (dao.updateTeachingTask(id, request) != 1) {
            throw new RuntimeException("Failed to update teaching task");
        }
    }

    public void deleteTeachingTask(Long id) {
        requireTeachingTask(id);
        if (dao.disableTeachingTask(id) != 1) {
            throw new RuntimeException("Failed to disable teaching task");
        }
    }

    public List<StudentCourseVO> listStudentCourses(Long studentId, Long teacherId) {
        return dao.listStudentCourses(studentId, teacherId);
    }

    public List<StudentCourseVO> listStudentCourses(Long studentId, Long teacherId, LoginUserVO currentUser) {
        if (AuthContext.hasAnyRole(currentUser, "ADMIN")) {
            return listStudentCourses(studentId, teacherId);
        }
        if (AuthContext.hasAnyRole(currentUser, "STUDENT")) {
            Long currentStudentId = currentStudent(currentUser).id();
            if (studentId != null && !studentId.equals(currentStudentId)) {
                throw new AuthException(403, "只能访问自己的选课记录");
            }
            return listStudentCourses(currentStudentId, null);
        }
        AuthContext.requireAnyRole(currentUser, "TEACHER");
        Long currentTeacherId = currentTeacher(currentUser).id();
        if (teacherId != null && !teacherId.equals(currentTeacherId)) {
            throw new AuthException(403, "只能访问自己的任课学生");
        }
        return listStudentCourses(null, currentTeacherId);
    }

    public List<SelectableTaskVO> listSelectableTasks(Long studentId, String semester) {
        requiredId(studentId, "Student id is required");
        if (isBlank(semester)) {
            throw new RuntimeException("Semester is required");
        }
        return dao.listSelectableTasks(studentId, semester);
    }

    public List<SelectableTaskVO> listSelectableTasks(Long studentId, String semester, LoginUserVO currentUser) {
        if (AuthContext.hasAnyRole(currentUser, "ADMIN")) {
            return listSelectableTasks(studentId, semester);
        }
        AuthContext.requireAnyRole(currentUser, "STUDENT");
        Long currentStudentId = currentStudent(currentUser).id();
        if (studentId != null && !studentId.equals(currentStudentId)) {
            throw new AuthException(403, "只能访问自己的可选课程");
        }
        return listSelectableTasks(currentStudentId, semester);
    }

    @Transactional
    public Long selectCourse(StudentCourseSelectRequest request) {
        Long studentId = requiredId(request.studentId(), "Student id is required");
        Long teachingTaskId = requiredId(request.teachingTaskId(), "Teaching task id is required");
        TeachingTaskVO task = requireTeachingTask(teachingTaskId);
        if (task.selectedCount() >= task.capacity()) {
            throw new RuntimeException("Course capacity is full");
        }
        AcademicBusinessDao.StudentCourseStatus existed = dao.findStudentCourseByStudentAndTask(studentId, teachingTaskId);
        if (existed != null && existed.status() == 1) {
            throw new RuntimeException("Course cannot be selected repeatedly");
        }
        if (hasCourseTime(task) && dao.countStudentCourseTimeConflict(
                studentId, task.semester(), task.weekday(), task.startSection(), task.endSection()) > 0) {
            throw new BusinessException("Selected course has a time conflict");
        }
        if (dao.increaseSelectedCountIfCapacityAvailable(teachingTaskId) != 1) {
            throw new RuntimeException("Course capacity is full");
        }
        if (existed == null) {
            dao.insertStudentCourse(studentId, teachingTaskId);
        } else {
            dao.reactivateStudentCourse(existed.id());
        }
        return dao.findStudentCourseByStudentAndTask(studentId, teachingTaskId).id();
    }

    public Long selectCourse(StudentCourseSelectRequest request, LoginUserVO currentUser) {
        AuthContext.requireAnyRole(currentUser, "STUDENT");
        Long currentStudentId = currentStudent(currentUser).id();
        if (request.studentId() != null && !request.studentId().equals(currentStudentId)) {
            throw new AuthException(403, "只能为自己选课");
        }
        return selectCourse(new StudentCourseSelectRequest(currentStudentId, request.teachingTaskId()));
    }

    @Transactional
    public void dropCourse(Long studentCourseId) {
        AcademicBusinessDao.StudentCourseDropRecord record = dao.findStudentCourseById(requiredId(studentCourseId, "Student course id is required"));
        if (record == null || record.status() != 1) {
            throw new RuntimeException("Student course record does not exist");
        }
        if ((record.gradeStatus() != null && record.gradeStatus() == 1) || record.score() != null) {
            throw new RuntimeException("Course with existing score cannot be dropped");
        }
        if (dao.disableStudentCourse(studentCourseId) != 1) {
            throw new RuntimeException("Failed to drop course");
        }
        if (dao.updateSelectedCount(record.teachingTaskId(), -1) != 1) {
            throw new RuntimeException("Failed to update selected count");
        }
    }

    public void dropCourse(Long studentCourseId, LoginUserVO currentUser) {
        AcademicBusinessDao.StudentCourseDropRecord record = requireStudentCourseRecord(studentCourseId);
        if (AuthContext.hasAnyRole(currentUser, "STUDENT") && !record.studentId().equals(currentStudent(currentUser).id())) {
            throw new AuthException(403, "只能退选自己的课程");
        }
        if (!AuthContext.hasAnyRole(currentUser, "ADMIN", "STUDENT")) {
            throw new AuthException(403, "无权退课");
        }
        dropCourse(studentCourseId);
    }

    public void updateScore(Long id, ScoreUpdateRequest request) {
        BigDecimal score = request.score();
        if (score.compareTo(BigDecimal.ZERO) < 0 || score.compareTo(new BigDecimal("100")) > 0) {
            throw new RuntimeException("Score must be between 0 and 100");
        }
        if (dao.updateScore(requiredId(id, "Student course id is required"), score, request.remark()) != 1) {
            throw new RuntimeException("Failed to update score");
        }
    }

    public void updateScore(Long id, ScoreUpdateRequest request, LoginUserVO currentUser) {
        requireScorePermission(id, currentUser);
        updateScore(id, request);
    }

    public void revokeScore(Long id) {
        if (dao.revokeScore(requiredId(id, "Student course id is required")) != 1) {
            throw new RuntimeException("Failed to revoke score");
        }
    }

    public void revokeScore(Long id, LoginUserVO currentUser) {
        requireScorePermission(id, currentUser);
        revokeScore(id);
    }

    public List<TeacherStudentVO> listTeacherStudents() {
        return dao.listTeacherStudents();
    }

    public void createTeacherStudent(TeacherStudentRequest request) {
        validateTeacherStudent(request);
        if (dao.insertTeacherStudent(request) != 1) {
            throw new RuntimeException("Failed to create teacher-student relation");
        }
    }

    public void updateTeacherStudent(Long id, TeacherStudentRequest request) {
        requiredId(id, "Relation id is required");
        validateTeacherStudent(request);
        if (dao.updateTeacherStudent(id, request) != 1) {
            throw new RuntimeException("Failed to update teacher-student relation");
        }
    }

    public void deleteTeacherStudent(Long id) {
        requiredId(id, "Relation id is required");
        if (dao.disableTeacherStudent(id) != 1) {
            throw new RuntimeException("Failed to disable teacher-student relation");
        }
    }

    public OverviewReportVO overviewReport() {
        return dao.overviewReport();
    }

    public List<NameValueReportVO> collegeStudentReport() {
        return dao.collegeStudentReport();
    }

    public List<NameValueReportVO> gradeDistributionReport() {
        return dao.gradeDistributionReport();
    }

    public List<TeachingLoadReportVO> teachingLoadReport() {
        return dao.teachingLoadReport();
    }

    private SysUserVO requireUser(Long id) {
        SysUserVO user = dao.findUserById(requiredId(id, "User id is required"));
        if (user == null) {
            throw new RuntimeException("User does not exist");
        }
        return user;
    }

    private StudentVO requireStudent(Long id) {
        StudentVO student = dao.findStudentById(requiredId(id, "Student id is required"));
        if (student == null) {
            throw new RuntimeException("Student does not exist");
        }
        return student;
    }

    private TeacherVO requireTeacher(Long id) {
        TeacherVO teacher = dao.findTeacherById(requiredId(id, "Teacher id is required"));
        if (teacher == null) {
            throw new RuntimeException("Teacher does not exist");
        }
        return teacher;
    }

    private TeachingTaskVO requireTeachingTask(Long id) {
        TeachingTaskVO task = dao.findTeachingTaskById(requiredId(id, "Teaching task id is required"));
        if (task == null) {
            throw new RuntimeException("Teaching task does not exist");
        }
        return task;
    }

    private Long insertUser(SysUserRequest user) {
        validateUser(user, null);
        if (dao.countUsername(user.username(), null) > 0) {
            throw new RuntimeException("Username already exists");
        }
        Long userId = dao.insertUser(user);
        if (userId == null) {
            throw new RuntimeException("Failed to create user");
        }
        return userId;
    }

    private void validateLogin(String username, String password, String role) {
        if (isBlank(username) || isBlank(password) || isBlank(role)) {
            throw new RuntimeException("Username, password and role are required");
        }
        if (!isValidRole(role)) {
            throw new RuntimeException("Invalid role");
        }
    }

    private void validateUser(SysUserRequest user, Long id) {
        if (user == null || isBlank(user.username())) {
            throw new RuntimeException("Username is required");
        }
        if (id == null && isBlank(user.password())) {
            throw new RuntimeException("Password is required");
        }
        if (!isValidRole(user.role())) {
            throw new RuntimeException("Invalid role");
        }
        if (user.status() != null && user.status() != 0 && user.status() != 1) {
            throw new RuntimeException("Invalid account status");
        }
    }

    private void validateStudent(StudentRequest student, Long userId) {
        requiredId(userId, "User id is required");
        requiredId(student.classId(), "Class id is required");
        if (isBlank(student.studentNo()) || isBlank(student.studentName()) || student.enrollmentYear() == null) {
            throw new RuntimeException("Required student fields are missing");
        }
    }

    private void validateTeacher(TeacherRequest teacher, Long userId) {
        requiredId(userId, "User id is required");
        requiredId(teacher.departmentId(), "Department id is required");
        if (isBlank(teacher.teacherNo()) || isBlank(teacher.teacherName())) {
            throw new RuntimeException("Required teacher fields are missing");
        }
    }

    private void validateTeachingTask(TeachingTaskRequest task) {
        requiredId(task.courseId(), "Course id is required");
        requiredId(task.teacherId(), "Teacher id is required");
        if (dao.countEnabledCourseById(task.courseId()) == 0) {
            throw new RuntimeException("Course is disabled or does not exist");
        }
        if (isBlank(task.semester())) {
            throw new RuntimeException("Semester is required");
        }
        if (task.weekday() == null || task.startSection() == null || task.endSection() == null) {
            throw new RuntimeException("Course time is required");
        }
        if (task.startSection() > task.endSection()) {
            throw new RuntimeException("Start section cannot be later than end section");
        }
    }

    private void validateTeacherStudent(TeacherStudentRequest relation) {
        requiredId(relation.teacherId(), "Teacher id is required");
        requiredId(relation.studentId(), "Student id is required");
        if (isBlank(relation.guideType())) {
            throw new RuntimeException("Guide type is required");
        }
    }

    private boolean hasCourseTime(TeachingTaskVO task) {
        return !isBlank(task.semester()) && task.weekday() != null
                && task.startSection() != null && task.endSection() != null;
    }

    private Long resolveUserId(Long userId, SysUserRequest account, String role) {
        if (userId != null) {
            return userId;
        }
        if (account == null) {
            throw new RuntimeException("User id is required");
        }
        return insertUser(normalizeUserRequest(account, role));
    }

    private SysUserRequest normalizeUserRequest(SysUserRequest request, String role) {
        return new SysUserRequest(request.username(), request.password(), role, request.status() == null ? 1 : request.status());
    }

    private Long requiredId(Long value, String message) {
        if (value == null) {
            throw new RuntimeException(message);
        }
        return value;
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private boolean isValidRole(String role) {
        return List.of("ADMIN", "TEACHER", "STUDENT").contains(role);
    }

    private void requireOwnerOrAdmin(LoginUserVO currentUser, Long userId) {
        if (AuthContext.hasAnyRole(currentUser, "ADMIN")) {
            return;
        }
        if (currentUser == null || !userId.equals(currentUser.getId())) {
            throw new AuthException(403, "只能访问自己的绑定信息");
        }
    }

    private StudentVO currentStudent(LoginUserVO currentUser) {
        AuthContext.requireAnyRole(currentUser, "STUDENT");
        return getStudentByUserId(currentUser.getId());
    }

    private TeacherVO currentTeacher(LoginUserVO currentUser) {
        AuthContext.requireAnyRole(currentUser, "TEACHER");
        return getTeacherByUserId(currentUser.getId());
    }

    private AcademicBusinessDao.StudentCourseDropRecord requireStudentCourseRecord(Long studentCourseId) {
        AcademicBusinessDao.StudentCourseDropRecord record = dao.findStudentCourseById(requiredId(studentCourseId, "Student course id is required"));
        if (record == null || record.status() != 1) {
            throw new RuntimeException("Student course record does not exist");
        }
        return record;
    }

    private void requireScorePermission(Long studentCourseId, LoginUserVO currentUser) {
        if (AuthContext.hasAnyRole(currentUser, "ADMIN")) {
            requireStudentCourseRecord(studentCourseId);
            return;
        }
        AuthContext.requireAnyRole(currentUser, "TEACHER");
        AcademicBusinessDao.StudentCourseDropRecord record = requireStudentCourseRecord(studentCourseId);
        TeachingTaskVO task = requireTeachingTask(record.teachingTaskId());
        if (!task.teacherId().equals(currentTeacher(currentUser).id())) {
            throw new AuthException(403, "只能维护自己任课课程的成绩");
        }
    }
}
