package com.tzufucius.edu.edumanagementsystem;

import com.tzufucius.edu.edumanagementsystem.dao.CollegeDao;
import com.tzufucius.edu.edumanagementsystem.entity.College;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class CollegeDaoTest {

    @Autowired
    private CollegeDao collegeDao;

    @Test
    void testFindAll() {
        List<College> colleges = collegeDao.findAll();

        assertNotNull(colleges);

        for (College college : colleges) {
            System.out.println(college.getId() + " - " + college.getCollegeCode() + " - " + college.getCollegeName());
        }
    }

    @Test
    void testInsert() {
        College college = new College();
        college.setCollegeCode("TEST-DAO-001");
        college.setCollegeName("DAO测试学院");
        college.setDescription("用于测试 DAO 新增");

        int rows = collegeDao.insert(college);

        assertEquals(1, rows);
    }

    @Test
    void testFindById() {
        College college = new College();
        college.setCollegeCode("TEST-DAO-002");
        college.setCollegeName("DAO查询测试学院");
        college.setDescription("用于测试 findById");

        collegeDao.insert(college);

        List<College> colleges = collegeDao.findAll();
        assertFalse(colleges.isEmpty());

        College firstCollege = colleges.get(0);
        College result = collegeDao.findById(firstCollege.getId());

        assertNotNull(result);
        assertEquals(firstCollege.getId(), result.getId());
    }

    @Test
    void testCountByCollegeCode() {
        College college = new College();
        college.setCollegeCode("TEST-DAO-003");
        college.setCollegeName("DAO编号统计测试学院");
        college.setDescription("用于测试 countByCollegeCode");

        collegeDao.insert(college);

        int count = collegeDao.countByCollegeCode("TEST-DAO-003");

        assertEquals(1, count);
    }

    @Test
    void testDisableById() {
        College college = new College();
        college.setCollegeCode("TEST-DAO-004");
        college.setCollegeName("DAO逻辑删除测试学院");
        college.setDescription("用于测试 disableById");

        collegeDao.insert(college);

        College inserted = collegeDao.findAll().get(0);
        int rows = collegeDao.disableById(inserted.getId());

        assertEquals(1, rows);
    }
}