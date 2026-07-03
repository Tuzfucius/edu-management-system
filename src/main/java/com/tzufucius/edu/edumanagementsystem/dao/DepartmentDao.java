package com.tzufucius.edu.edumanagementsystem.dao;

import com.tzufucius.edu.edumanagementsystem.entity.Department;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public class DepartmentDao {

    private final JdbcTemplate jdbcTemplate;

    /**
     * 注入 Spring JDBC 操作对象。
     *
     * @param jdbcTemplate Spring 提供的数据库访问工具
     */
    public DepartmentDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * 将 department 表的一行查询结果转换为 Department 实体。
     *
     * @param rs 查询结果集
     * @param rowNum 当前行号
     * @return 转换后的教研室实体
     * @throws java.sql.SQLException 读取结果集失败时抛出
     */
    private Department mapRow(java.sql.ResultSet rs, int rowNum) throws java.sql.SQLException {
        Department department = new Department();

        department.setId(rs.getLong("id"));
        department.setCollegeId(rs.getLong("college_id"));
        department.setDepartmentCode(rs.getString("department_code"));
        department.setDepartmentName(rs.getString("department_name"));
        department.setOfficeLocation(rs.getString("office_location"));
        department.setStatus(rs.getInt("status"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        Timestamp updatedAt = rs.getTimestamp("updated_at");

        if (createdAt != null) {
            department.setCreatedAt(createdAt.toLocalDateTime());
        }

        if (updatedAt != null) {
            department.setUpdatedAt(updatedAt.toLocalDateTime());
        }

        return department;
    }

    /**
     * 查询全部正常教研室。
     *
     * @return 正常状态的教研室列表
     */
    public List<Department> findAll() {
        String sql = """
                SELECT id, college_id, department_code, department_name, office_location,
                       status, created_at, updated_at
                FROM department
                WHERE status = 1
                ORDER BY id DESC
                """;
        return jdbcTemplate.query(sql, this::mapRow);
    }

    /**
     * 根据 id 查询单个教研室。
     *
     * @param id 教研室ID
     * @return 教研室存在且状态正常时返回教研室实体，否则返回 null
     */
    public Department findById(Long id) {
        String sql = """
                SELECT id, college_id, department_code, department_name, office_location,
                       status, created_at, updated_at
                FROM department
                WHERE id = ? AND status = 1
                """;

        List<Department> departments = jdbcTemplate.query(sql, this::mapRow, id);

        if (departments.isEmpty()) {
            return null;
        }

        return departments.get(0);
    }

    /**
     * 新增教研室。
     *
     * @param department 待新增的教研室信息
     * @return 受影响行数
     */
    public int insert(Department department) {
        String sql = """
                INSERT INTO department(college_id, department_code, department_name, office_location, status)
                VALUES (?, ?, ?, ?, 1)
                """;

        return jdbcTemplate.update(
                sql,
                department.getCollegeId(),
                department.getDepartmentCode(),
                department.getDepartmentName(),
                department.getOfficeLocation()
        );
    }

    /**
     * 修改教研室。
     *
     * @param department 待修改的教研室信息，必须包含教研室ID
     * @return 受影响行数
     */
    public int update(Department department) {
        String sql = """
                UPDATE department
                SET college_id = ?, department_code = ?, department_name = ?, office_location = ?
                WHERE id = ? AND status = 1
                """;

        return jdbcTemplate.update(
                sql,
                department.getCollegeId(),
                department.getDepartmentCode(),
                department.getDepartmentName(),
                department.getOfficeLocation(),
                department.getId()
        );
    }

    /**
     * 逻辑删除教研室。
     *
     * @param id 教研室ID
     * @return 受影响行数
     */
    public int disableById(Long id) {
        String sql = """
                UPDATE department
                SET status = 0
                WHERE id = ?
                """;

        return jdbcTemplate.update(sql, id);
    }

    /**
     * 判断教研室编号是否重复。
     *
     * @param departmentCode 教研室编号
     * @return 使用该编号的教研室数量
     */
    public int countByDepartmentCode(String departmentCode) {
        String sql = """
                SELECT COUNT(*)
                FROM department
                WHERE department_code = ?
                """;

        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, departmentCode);

        return count == null ? 0 : count;
    }

    /**
     * 判断除指定教研室外是否存在相同教研室编号。
     *
     * @param departmentCode 教研室编号
     * @param excludeId 需要排除的教研室ID
     * @return 除指定教研室外使用该编号的教研室数量
     */
    public int countByDepartmentCodeExcludeId(String departmentCode, Long excludeId) {
        String sql = """
                SELECT COUNT(*)
                FROM department
                WHERE department_code = ? AND id <> ?
                """;

        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, departmentCode, excludeId);

        return count == null ? 0 : count;
    }

    /**
     * 判断教研室下是否还有教师。
     *
     * @param departmentId 教研室ID
     * @return 该教研室下正常状态的教师数量
     */
    public int countTeacherByDepartmentId(Long departmentId) {
        String sql = """
                SELECT COUNT(*)
                FROM teacher
                WHERE department_id = ? AND status = 1
                """;

        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, departmentId);

        return count == null ? 0 : count;
    }
}
