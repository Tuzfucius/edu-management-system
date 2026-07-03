package com.tzufucius.edu.edumanagementsystem;

import com.tzufucius.edu.edumanagementsystem.dao.CollegeDao;
import com.tzufucius.edu.edumanagementsystem.dao.MajorDao;
import com.tzufucius.edu.edumanagementsystem.entity.College;
import com.tzufucius.edu.edumanagementsystem.entity.Major;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MajorDaoTest {

    @Autowired
    private MajorDao majorDao;

    @Autowired
    private CollegeDao collegeDao;

    @Test
    void testFindAll() {
        List<Major> majors = majorDao.findAll();

        assertNotNull(majors);
    }

    @Test
    void testInsert() {
        Major major = createMajor("TEST-MAJOR-DAO-001", "DAO测试专业");

        int rows = majorDao.insert(major);

        assertEquals(1, rows);
    }

    @Test
    void testFindById() {
        Major major = createMajor("TEST-MAJOR-DAO-002", "DAO查询测试专业");
        majorDao.insert(major);

        Major inserted = majorDao.findAll().get(0);
        Major result = majorDao.findById(inserted.getId());

        assertNotNull(result);
        assertEquals(inserted.getId(), result.getId());
    }

    @Test
    void testCountByMajorCode() {
        Major major = createMajor("TEST-MAJOR-DAO-003", "DAO编号统计测试专业");
        majorDao.insert(major);

        int count = majorDao.countByMajorCode("TEST-MAJOR-DAO-003");

        assertEquals(1, count);
    }

    @Test
    void testCountByMajorCodeExcludeId() {
        Major major = createMajor("TEST-MAJOR-DAO-004", "DAO排除自身编号测试专业");
        majorDao.insert(major);

        Major inserted = majorDao.findAll().get(0);
        int count = majorDao.countByMajorCodeExcludeId("TEST-MAJOR-DAO-004", inserted.getId());

        assertEquals(0, count);
    }

    @Test
    void testDisableById() {
        Major major = createMajor("TEST-MAJOR-DAO-005", "DAO逻辑删除测试专业");
        majorDao.insert(major);

        Major inserted = majorDao.findAll().get(0);
        int rows = majorDao.disableById(inserted.getId());

        assertEquals(1, rows);
    }

    @Test
    void testCountClassByMajorId() {
        Major major = createMajor("TEST-MAJOR-DAO-006", "DAO班级数量测试专业");
        majorDao.insert(major);

        Major inserted = majorDao.findAll().get(0);
        int count = majorDao.countClassByMajorId(inserted.getId());

        assertEquals(0, count);
    }

    private Major createMajor(String code, String name) {
        College college = new College();
        college.setCollegeCode("C" + System.nanoTime());
        college.setCollegeName(name + "所属学院");
        college.setDescription("用于专业 DAO 测试");
        collegeDao.insert(college);

        Major major = new Major();
        major.setCollegeId(collegeDao.findAll().get(0).getId());
        major.setMajorCode(code);
        major.setMajorName(name);
        major.setSchoolingYears(4);
        major.setDegreeType("工学");
        return major;
    }
}
