package com.tzufucius.edu.edumanagementsystem;

import com.tzufucius.edu.edumanagementsystem.dao.ClassInfoDao;
import com.tzufucius.edu.edumanagementsystem.dao.CollegeDao;
import com.tzufucius.edu.edumanagementsystem.dao.MajorDao;
import com.tzufucius.edu.edumanagementsystem.entity.ClassInfo;
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
class ClassInfoDaoTest {

    @Autowired
    private ClassInfoDao classInfoDao;

    @Autowired
    private MajorDao majorDao;

    @Autowired
    private CollegeDao collegeDao;

    @Test
    void testFindAll() {
        List<ClassInfo> classInfos = classInfoDao.findAll();

        assertNotNull(classInfos);
    }

    @Test
    void testInsert() {
        ClassInfo classInfo = createClassInfo("TEST-CLASS-DAO-001", "DAO测试班级");

        int rows = classInfoDao.insert(classInfo);

        assertEquals(1, rows);
    }

    @Test
    void testFindById() {
        ClassInfo classInfo = createClassInfo("TEST-CLASS-DAO-002", "DAO查询测试班级");
        classInfoDao.insert(classInfo);

        ClassInfo inserted = classInfoDao.findAll().get(0);
        ClassInfo result = classInfoDao.findById(inserted.getId());

        assertNotNull(result);
        assertEquals(inserted.getId(), result.getId());
    }

    @Test
    void testCountByClassCode() {
        ClassInfo classInfo = createClassInfo("TEST-CLASS-DAO-003", "DAO编号统计测试班级");
        classInfoDao.insert(classInfo);

        int count = classInfoDao.countByClassCode("TEST-CLASS-DAO-003");

        assertEquals(1, count);
    }

    @Test
    void testCountByClassCodeExcludeId() {
        ClassInfo classInfo = createClassInfo("TEST-CLASS-DAO-004", "DAO排除自身编号测试班级");
        classInfoDao.insert(classInfo);

        ClassInfo inserted = classInfoDao.findAll().get(0);
        int count = classInfoDao.countByClassCodeExcludeId("TEST-CLASS-DAO-004", inserted.getId());

        assertEquals(0, count);
    }

    @Test
    void testDisableById() {
        ClassInfo classInfo = createClassInfo("TEST-CLASS-DAO-005", "DAO逻辑删除测试班级");
        classInfoDao.insert(classInfo);

        ClassInfo inserted = classInfoDao.findAll().get(0);
        int rows = classInfoDao.disableById(inserted.getId());

        assertEquals(1, rows);
    }

    @Test
    void testCountStudentByClassId() {
        ClassInfo classInfo = createClassInfo("TEST-CLASS-DAO-006", "DAO学生数量测试班级");
        classInfoDao.insert(classInfo);

        ClassInfo inserted = classInfoDao.findAll().get(0);
        int count = classInfoDao.countStudentByClassId(inserted.getId());

        assertEquals(0, count);
    }

    private ClassInfo createClassInfo(String code, String name) {
        Long majorId = insertMajor(name);

        ClassInfo classInfo = new ClassInfo();
        classInfo.setMajorId(majorId);
        classInfo.setClassCode(code);
        classInfo.setClassName(name);
        classInfo.setEntranceYear(2024);
        return classInfo;
    }

    private Long insertMajor(String name) {
        College college = new College();
        college.setCollegeCode("C" + System.nanoTime());
        college.setCollegeName(name + "所属学院");
        college.setDescription("用于班级 DAO 测试");
        collegeDao.insert(college);

        Major major = new Major();
        major.setCollegeId(collegeDao.findAll().get(0).getId());
        major.setMajorCode("M" + System.nanoTime());
        major.setMajorName(name + "所属专业");
        major.setSchoolingYears(4);
        major.setDegreeType("工学");
        majorDao.insert(major);

        return majorDao.findAll().get(0).getId();
    }
}
