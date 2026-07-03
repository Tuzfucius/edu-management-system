package com.tzufucius.edu.edumanagementsystem.dao;

import com.tzufucius.edu.edumanagementsystem.entity.ClassInfo;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public class ClassInfoDao {

    private final JdbcTemplate jdbcTemplate;

    /**
     * 注入 Spring JDBC 操作对象。
     *
     * @param jdbcTemplate Spring 提供的数据库访问工具
     */
    public ClassInfoDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * 将 class_info 表的一行查询结果转换为 ClassInfo 实体。
     *
     * @param rs 查询结果集
     * @param rowNum 当前行号
     * @return 转换后的班级实体
     * @throws java.sql.SQLException 读取结果集失败时抛出
     */
    private ClassInfo mapRow(java.sql.ResultSet rs, int rowNum) throws java.sql.SQLException {
        ClassInfo classInfo = new ClassInfo();

        classInfo.setId(rs.getLong("id"));
        classInfo.setMajorId(rs.getLong("major_id"));
        classInfo.setClassCode(rs.getString("class_code"));
        classInfo.setClassName(rs.getString("class_name"));
        classInfo.setEntranceYear(rs.getInt("entrance_year"));
        classInfo.setStatus(rs.getInt("status"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        Timestamp updatedAt = rs.getTimestamp("updated_at");

        if (createdAt != null) {
            classInfo.setCreatedAt(createdAt.toLocalDateTime());
        }

        if (updatedAt != null) {
            classInfo.setUpdatedAt(updatedAt.toLocalDateTime());
        }

        return classInfo;
    }

    /**
     * 查询全部正常班级。
     *
     * @return 正常状态的班级列表
     */
    public List<ClassInfo> findAll() {
        String sql = """
                SELECT id, major_id, class_code, class_name, entrance_year, status, created_at, updated_at
                FROM class_info
                WHERE status = 1
                ORDER BY id DESC
                """;
        return jdbcTemplate.query(sql, this::mapRow);
    }

    /**
     * 根据 id 查询单个班级。
     *
     * @param id 班级ID
     * @return 班级存在且状态正常时返回班级实体，否则返回 null
     */
    public ClassInfo findById(Long id) {
        String sql = """
                SELECT id, major_id, class_code, class_name, entrance_year, status, created_at, updated_at
                FROM class_info
                WHERE id = ? AND status = 1
                """;

        List<ClassInfo> classInfos = jdbcTemplate.query(sql, this::mapRow, id);

        if (classInfos.isEmpty()) {
            return null;
        }

        return classInfos.get(0);
    }

    /**
     * 新增班级。
     *
     * @param classInfo 待新增的班级信息
     * @return 受影响行数
     */
    public int insert(ClassInfo classInfo) {
        String sql = """
                INSERT INTO class_info(major_id, class_code, class_name, entrance_year, status)
                VALUES (?, ?, ?, ?, 1)
                """;

        return jdbcTemplate.update(
                sql,
                classInfo.getMajorId(),
                classInfo.getClassCode(),
                classInfo.getClassName(),
                classInfo.getEntranceYear()
        );
    }

    /**
     * 修改班级。
     *
     * @param classInfo 待修改的班级信息，必须包含班级ID
     * @return 受影响行数
     */
    public int update(ClassInfo classInfo) {
        String sql = """
                UPDATE class_info
                SET major_id = ?, class_code = ?, class_name = ?, entrance_year = ?
                WHERE id = ? AND status = 1
                """;

        return jdbcTemplate.update(
                sql,
                classInfo.getMajorId(),
                classInfo.getClassCode(),
                classInfo.getClassName(),
                classInfo.getEntranceYear(),
                classInfo.getId()
        );
    }

    /**
     * 逻辑删除班级。
     *
     * @param id 班级ID
     * @return 受影响行数
     */
    public int disableById(Long id) {
        String sql = """
                UPDATE class_info
                SET status = 0
                WHERE id = ?
                """;

        return jdbcTemplate.update(sql, id);
    }

    /**
     * 判断班级编号是否重复。
     *
     * @param classCode 班级编号
     * @return 使用该编号的班级数量
     */
    public int countByClassCode(String classCode) {
        String sql = """
                SELECT COUNT(*)
                FROM class_info
                WHERE class_code = ?
                """;

        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, classCode);

        return count == null ? 0 : count;
    }

    /**
     * 判断除指定班级外是否存在相同班级编号。
     *
     * @param classCode 班级编号
     * @param excludeId 需要排除的班级ID
     * @return 除指定班级外使用该编号的班级数量
     */
    public int countByClassCodeExcludeId(String classCode, Long excludeId) {
        String sql = """
                SELECT COUNT(*)
                FROM class_info
                WHERE class_code = ? AND id <> ?
                """;

        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, classCode, excludeId);

        return count == null ? 0 : count;
    }

    /**
     * 判断班级下是否还有学生。
     *
     * @param classId 班级ID
     * @return 该班级下正常状态的学生数量
     */
    public int countStudentByClassId(Long classId) {
        String sql = """
                SELECT COUNT(*)
                FROM student
                WHERE class_id = ? AND status = 1
                """;

        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, classId);

        return count == null ? 0 : count;
    }
}
