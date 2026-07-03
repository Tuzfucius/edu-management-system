package com.tzufucius.edu.edumanagementsystem;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class DatabaseConnectionTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void testConnection() {
        assertNotNull(jdbcTemplate, "JdbcTemplate 不应该为空");

        Integer tableCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = DATABASE()",
                Integer.class
        );

        Integer collegeCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM college",
                Integer.class
        );

        assertNotNull(tableCount, "数据库表数量查询结果不应该为空");
        assertTrue(tableCount > 0, "当前数据库中应该至少存在一张表");

        assertNotNull(collegeCount, "college 表记录数不应该为空");
        assertTrue(collegeCount >= 0, "college 表记录数不能为负数");

        System.out.println("当前数据库表数量：" + tableCount);
        System.out.println("college 表数据数量：" + collegeCount);
    }
}