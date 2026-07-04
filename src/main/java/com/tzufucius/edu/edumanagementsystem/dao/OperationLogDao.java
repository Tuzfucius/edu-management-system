package com.tzufucius.edu.edumanagementsystem.dao;

import com.tzufucius.edu.edumanagementsystem.common.PageResult;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
public class OperationLogDao {
    private final JdbcTemplate jdbcTemplate;

    public OperationLogDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public int insert(Long userId, String moduleName, String operationType, String targetTable,
                      Long targetId, String description, String ipAddress) {
        return jdbcTemplate.update("""
                INSERT INTO operation_log(user_id, module_name, operation_type, target_table, target_id, description, ip_address)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """, userId, moduleName, operationType, targetTable, targetId, description, ipAddress);
    }

    public PageResult<Map<String, Object>> page(String moduleName, String operationType, Long userId,
                                                String startTime, String endTime, int page, int size) {
        QueryParts queryParts = buildQuery(moduleName, operationType, userId, startTime, endTime);
        Long total = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) " + queryParts.fromAndWhere(),
                Long.class,
                queryParts.args().toArray()
        );

        List<Object> pageArgs = new ArrayList<>(queryParts.args());
        pageArgs.add(size);
        pageArgs.add((page - 1) * size);
        List<Map<String, Object>> records = jdbcTemplate.queryForList("""
                SELECT ol.id, ol.user_id AS userId, u.username,
                       ol.module_name AS moduleName, ol.operation_type AS operationType,
                       ol.target_table AS targetTable, ol.target_id AS targetId,
                       ol.description, ol.ip_address AS ipAddress, ol.created_at AS createdAt
                """ + queryParts.fromAndWhere() + " ORDER BY ol.created_at DESC, ol.id DESC LIMIT ? OFFSET ?",
                pageArgs.toArray()
        );

        return PageResult.of(records, total == null ? 0 : total, page, size);
    }

    private QueryParts buildQuery(String moduleName, String operationType, Long userId, String startTime, String endTime) {
        StringBuilder sql = new StringBuilder("""
                FROM operation_log ol
                LEFT JOIN sys_user u ON u.id = ol.user_id
                WHERE 1 = 1
                """);
        List<Object> args = new ArrayList<>();
        if (moduleName != null && !moduleName.isBlank()) {
            sql.append(" AND ol.module_name = ?");
            args.add(moduleName);
        }
        if (operationType != null && !operationType.isBlank()) {
            sql.append(" AND ol.operation_type = ?");
            args.add(operationType);
        }
        if (userId != null) {
            sql.append(" AND ol.user_id = ?");
            args.add(userId);
        }
        if (startTime != null && !startTime.isBlank()) {
            sql.append(" AND ol.created_at >= ?");
            args.add(startTime);
        }
        if (endTime != null && !endTime.isBlank()) {
            sql.append(" AND ol.created_at <= ?");
            args.add(endTime);
        }
        return new QueryParts(sql.toString(), args);
    }

    private record QueryParts(String fromAndWhere, List<Object> args) {
    }
}

