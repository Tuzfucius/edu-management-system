package com.tzufucius.edu.edumanagementsystem.dao;

import com.tzufucius.edu.edumanagementsystem.entity.Course;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public class CourseDao {

    private final JdbcTemplate jdbcTemplate;

    /**
     * 注入 Spring JDBC 操作对象。
     *
     * @param jdbcTemplate Spring 提供的数据库访问工具
     */
    public CourseDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * 将 course 表的一行查询结果转换为 Course 实体。
     *
     * @param rs 查询结果集
     * @param rowNum 当前行号
     * @return 转换后的课程实体
     * @throws java.sql.SQLException 读取结果集失败时抛出
     */
    private Course mapRow(java.sql.ResultSet rs, int rowNum) throws java.sql.SQLException {
        Course course = new Course();

        course.setId(rs.getLong("id"));
        course.setCourseCode(rs.getString("course_code"));
        course.setCourseName(rs.getString("course_name"));
        course.setCredit(rs.getBigDecimal("credit"));
        course.setTotalHours((Integer) rs.getObject("total_hours"));
        course.setCourseType(rs.getString("course_type"));
        course.setExamType(rs.getString("exam_type"));
        course.setStatus(rs.getInt("status"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        Timestamp updatedAt = rs.getTimestamp("updated_at");

        if (createdAt != null) {
            course.setCreatedAt(createdAt.toLocalDateTime());
        }

        if (updatedAt != null) {
            course.setUpdatedAt(updatedAt.toLocalDateTime());
        }

        return course;
    }

    /**
     * 查询全部正常课程。
     *
     * @return 正常状态的课程列表
     */
    public List<Course> findAll() {
        String sql = """
                SELECT id, course_code, course_name, credit, total_hours, course_type, exam_type,
                       status, created_at, updated_at
                FROM course
                WHERE status = 1
                ORDER BY id DESC
                """;
        return jdbcTemplate.query(sql, this::mapRow);
    }

    /**
     * 根据 id 查询单个课程。
     *
     * @param id 课程ID
     * @return 课程存在且状态正常时返回课程实体，否则返回 null
     */
    public Course findById(Long id) {
        String sql = """
                SELECT id, course_code, course_name, credit, total_hours, course_type, exam_type,
                       status, created_at, updated_at
                FROM course
                WHERE id = ? AND status = 1
                """;

        List<Course> courses = jdbcTemplate.query(sql, this::mapRow, id);

        if (courses.isEmpty()) {
            return null;
        }

        return courses.get(0);
    }

    /**
     * 新增课程。
     *
     * @param course 待新增的课程信息
     * @return 受影响行数
     */
    public int insert(Course course) {
        String sql = """
                INSERT INTO course(course_code, course_name, credit, total_hours, course_type, exam_type, status)
                VALUES (?, ?, ?, ?, ?, ?, 1)
                """;

        return jdbcTemplate.update(
                sql,
                course.getCourseCode(),
                course.getCourseName(),
                course.getCredit(),
                course.getTotalHours(),
                course.getCourseType(),
                course.getExamType()
        );
    }

    /**
     * 修改课程。
     *
     * @param course 待修改的课程信息，必须包含课程ID
     * @return 受影响行数
     */
    public int update(Course course) {
        String sql = """
                UPDATE course
                SET course_code = ?, course_name = ?, credit = ?, total_hours = ?, course_type = ?, exam_type = ?
                WHERE id = ? AND status = 1
                """;

        return jdbcTemplate.update(
                sql,
                course.getCourseCode(),
                course.getCourseName(),
                course.getCredit(),
                course.getTotalHours(),
                course.getCourseType(),
                course.getExamType(),
                course.getId()
        );
    }

    /**
     * 逻辑删除课程。
     *
     * @param id 课程ID
     * @return 受影响行数
     */
    public int disableById(Long id) {
        String sql = """
                UPDATE course
                SET status = 0
                WHERE id = ?
                """;

        return jdbcTemplate.update(sql, id);
    }

    /**
     * 判断课程编号是否重复。
     *
     * @param courseCode 课程编号
     * @return 使用该编号的课程数量
     */
    public int countByCourseCode(String courseCode) {
        String sql = """
                SELECT COUNT(*)
                FROM course
                WHERE course_code = ?
                """;

        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, courseCode);

        return count == null ? 0 : count;
    }

    /**
     * 判断除指定课程外是否存在相同课程编号。
     *
     * @param courseCode 课程编号
     * @param excludeId 需要排除的课程ID
     * @return 除指定课程外使用该编号的课程数量
     */
    public int countByCourseCodeExcludeId(String courseCode, Long excludeId) {
        String sql = """
                SELECT COUNT(*)
                FROM course
                WHERE course_code = ? AND id <> ?
                """;

        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, courseCode, excludeId);

        return count == null ? 0 : count;
    }

    /**
     * 判断课程是否已有任课安排。
     *
     * @param courseId 课程ID
     * @return 该课程下未停用任课安排数量
     */
    public int countTeachingTaskByCourseId(Long courseId) {
        String sql = """
                SELECT COUNT(*)
                FROM teaching_task
                WHERE course_id = ? AND task_status <> 0
                """;

        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, courseId);

        return count == null ? 0 : count;
    }
}
