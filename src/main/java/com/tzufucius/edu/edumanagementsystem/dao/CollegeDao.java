package com.tzufucius.edu.edumanagementsystem.dao;

import com.tzufucius.edu.edumanagementsystem.entity.College;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public class CollegeDao {
    private final JdbcTemplate jdbcTemplate;

    /**
     * 注入 Spring JDBC 操作对象。
     *
     * @param jdbcTemplate Spring 提供的数据库访问工具
     */
    public CollegeDao(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * 将 college 表的一行查询结果转换为 College 实体。
     *
     * @param rs 查询结果集
     * @param rowNum 当前行号
     * @return 转换后的学院实体
     * @throws java.sql.SQLException 读取结果集失败时抛出
     */
    private College mapRow(java.sql.ResultSet rs, int rowNum) throws java.sql.SQLException{
        College college = new College();

        college.setId(rs.getLong("id"));
        college.setCollegeCode(rs.getString("college_code"));
        college.setCollegeName(rs.getString("college_name"));
        college.setDescription(rs.getString("description"));
        college.setStatus(rs.getInt("status"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        Timestamp updatedAt = rs.getTimestamp("updated_at");

        if (createdAt != null) {
            college.setCreatedAt(createdAt.toLocalDateTime());
        }

        if (updatedAt != null) {
            college.setUpdatedAt(updatedAt.toLocalDateTime());
        }

        return college;
    }

    /**
     * 查询全部正常学院
     *
     * @return 正常状态的学院列表
     */
    public List<College> findAll(){
        String sql = """
                SELECT id, college_code, college_name, description, status, created_at, updated_at
                FROM college
                WHERE status = 1
                ORDER BY id DESC
                """;
        return jdbcTemplate.query(sql, this::mapRow);
    }

    /**
     * 根据 id 查询单个学院
     *
     * @param id 学院ID
     * @return 学院存在且状态正常时返回学院实体，否则返回 null
     */
    public College findById(Long id) {
        String sql = """
                SELECT id, college_code, college_name, description, status, created_at, updated_at
                FROM college
                WHERE id = ? AND status = 1
                """;

        List<College> colleges = jdbcTemplate.query(sql, this::mapRow, id);

        if (colleges.isEmpty()) {
            return null;
        }

        return colleges.get(0);
    }

    /**
     * 新增学院
     *
     * @param college 待新增的学院信息
     * @return 受影响行数
     */
    public int insert(College college) {
        String sql = """
                INSERT INTO college(college_code, college_name, description, status)
                VALUES (?, ?, ?, 1)
                """;

        return jdbcTemplate.update(
                sql,
                college.getCollegeCode(),
                college.getCollegeName(),
                college.getDescription()
        );
    }

    /**
     * 修改学院
     *
     * @param college 待修改的学院信息，必须包含学院ID
     * @return 受影响行数
     */
    public int update(College college) {
        String sql = """
                UPDATE college
                SET college_code = ?, college_name = ?, description = ?
                WHERE id = ? AND status = 1
                """;

        return jdbcTemplate.update(
                sql,
                college.getCollegeCode(),
                college.getCollegeName(),
                college.getDescription(),
                college.getId()
        );
    }

    /**
     * 逻辑删除学院
     *
     * @param id 学院ID
     * @return 受影响行数
     */
    public int disableById(Long id) {
        String sql = """
                UPDATE college
                SET status = 0
                WHERE id = ?
                """;

        return jdbcTemplate.update(sql, id);
    }

    /**
     * 判断学院编号是否重复
     *
     * @param collegeCode 学院编号
     * @return 使用该编号的学院数量
     */
    public int countByCollegeCode(String collegeCode) {
        String sql = """
                SELECT COUNT(*)
                FROM college
                WHERE college_code = ?
                """;

        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, collegeCode);

        return count == null ? 0 : count;
    }

    /**
     * 判断除指定学院外是否存在相同学院编号。
     *
     * @param collegeCode 学院编号
     * @param excludeId 需要排除的学院ID
     * @return 除指定学院外使用该编号的学院数量
     */
    public int countByCollegeCodeExcludeId(String collegeCode, Long excludeId) {
        String sql = """
                SELECT COUNT(*)
                FROM college
                WHERE college_code = ? AND id <> ?
                """;

        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, collegeCode, excludeId);

        return count == null ? 0 : count;
    }

    /**
     * 判断学院下是否还有专业
     *
     * @param collegeId 学院ID
     * @return 该学院下正常状态的专业数量
     */
    public int countMajorByCollegeId(Long collegeId) {
        String sql = """
                SELECT COUNT(*)
                FROM major
                WHERE college_id = ? AND status = 1
                """;

        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, collegeId);

        return count == null ? 0 : count;
    }
}
