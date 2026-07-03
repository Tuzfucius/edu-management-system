package com.tzufucius.edu.edumanagementsystem.dao;

import com.tzufucius.edu.edumanagementsystem.entity.Major;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public class MajorDao {

    private final JdbcTemplate jdbcTemplate;

    /**
     * 注入 Spring JDBC 操作对象。
     *
     * @param jdbcTemplate Spring 提供的数据库访问工具
     */
    public MajorDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * 将 major 表的一行查询结果转换为 Major 实体。
     *
     * @param rs 查询结果集
     * @param rowNum 当前行号
     * @return 转换后的专业实体
     * @throws java.sql.SQLException 读取结果集失败时抛出
     */
    private Major mapRow(java.sql.ResultSet rs, int rowNum) throws java.sql.SQLException {
        Major major = new Major();

        major.setId(rs.getLong("id"));
        major.setCollegeId(rs.getLong("college_id"));
        major.setMajorCode(rs.getString("major_code"));
        major.setMajorName(rs.getString("major_name"));
        major.setSchoolingYears(rs.getInt("schooling_years"));
        major.setDegreeType(rs.getString("degree_type"));
        major.setStatus(rs.getInt("status"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        Timestamp updatedAt = rs.getTimestamp("updated_at");

        if (createdAt != null) {
            major.setCreatedAt(createdAt.toLocalDateTime());
        }

        if (updatedAt != null) {
            major.setUpdatedAt(updatedAt.toLocalDateTime());
        }

        return major;
    }

    /**
     * 查询全部正常专业。
     *
     * @return 正常状态的专业列表
     */
    public List<Major> findAll() {
        String sql = """
                SELECT id, college_id, major_code, major_name, schooling_years, degree_type,
                       status, created_at, updated_at
                FROM major
                WHERE status = 1
                ORDER BY id DESC
                """;
        return jdbcTemplate.query(sql, this::mapRow);
    }

    /**
     * 根据 id 查询单个专业。
     *
     * @param id 专业ID
     * @return 专业存在且状态正常时返回专业实体，否则返回 null
     */
    public Major findById(Long id) {
        String sql = """
                SELECT id, college_id, major_code, major_name, schooling_years, degree_type,
                       status, created_at, updated_at
                FROM major
                WHERE id = ? AND status = 1
                """;

        List<Major> majors = jdbcTemplate.query(sql, this::mapRow, id);

        if (majors.isEmpty()) {
            return null;
        }

        return majors.get(0);
    }

    /**
     * 新增专业。
     *
     * @param major 待新增的专业信息
     * @return 受影响行数
     */
    public int insert(Major major) {
        String sql = """
                INSERT INTO major(college_id, major_code, major_name, schooling_years, degree_type, status)
                VALUES (?, ?, ?, ?, ?, 1)
                """;

        return jdbcTemplate.update(
                sql,
                major.getCollegeId(),
                major.getMajorCode(),
                major.getMajorName(),
                major.getSchoolingYears(),
                major.getDegreeType()
        );
    }

    /**
     * 修改专业。
     *
     * @param major 待修改的专业信息，必须包含专业ID
     * @return 受影响行数
     */
    public int update(Major major) {
        String sql = """
                UPDATE major
                SET college_id = ?, major_code = ?, major_name = ?, schooling_years = ?, degree_type = ?
                WHERE id = ? AND status = 1
                """;

        return jdbcTemplate.update(
                sql,
                major.getCollegeId(),
                major.getMajorCode(),
                major.getMajorName(),
                major.getSchoolingYears(),
                major.getDegreeType(),
                major.getId()
        );
    }

    /**
     * 逻辑删除专业。
     *
     * @param id 专业ID
     * @return 受影响行数
     */
    public int disableById(Long id) {
        String sql = """
                UPDATE major
                SET status = 0
                WHERE id = ?
                """;

        return jdbcTemplate.update(sql, id);
    }

    /**
     * 判断专业编号是否重复。
     *
     * @param majorCode 专业编号
     * @return 使用该编号的专业数量
     */
    public int countByMajorCode(String majorCode) {
        String sql = """
                SELECT COUNT(*)
                FROM major
                WHERE major_code = ?
                """;

        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, majorCode);

        return count == null ? 0 : count;
    }

    /**
     * 判断除指定专业外是否存在相同专业编号。
     *
     * @param majorCode 专业编号
     * @param excludeId 需要排除的专业ID
     * @return 除指定专业外使用该编号的专业数量
     */
    public int countByMajorCodeExcludeId(String majorCode, Long excludeId) {
        String sql = """
                SELECT COUNT(*)
                FROM major
                WHERE major_code = ? AND id <> ?
                """;

        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, majorCode, excludeId);

        return count == null ? 0 : count;
    }

    /**
     * 判断专业下是否还有班级。
     *
     * @param majorId 专业ID
     * @return 该专业下正常状态的班级数量
     */
    public int countClassByMajorId(Long majorId) {
        String sql = """
                SELECT COUNT(*)
                FROM class_info
                WHERE major_id = ? AND status = 1
                """;

        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, majorId);

        return count == null ? 0 : count;
    }
}
