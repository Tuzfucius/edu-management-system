package com.tzufucius.edu.edumanagementsystem;

import com.tzufucius.edu.edumanagementsystem.dao.ClassInfoDao;
import com.tzufucius.edu.edumanagementsystem.dao.CollegeDao;
import com.tzufucius.edu.edumanagementsystem.dao.MajorDao;
import com.tzufucius.edu.edumanagementsystem.entity.ClassInfo;
import com.tzufucius.edu.edumanagementsystem.entity.College;
import com.tzufucius.edu.edumanagementsystem.entity.Major;
import com.tzufucius.edu.edumanagementsystem.service.ClassInfoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ClassInfoServiceTest {

    @Autowired
    private ClassInfoService classInfoService;

    @Autowired
    private ClassInfoDao classInfoDao;

    @Autowired
    private MajorDao majorDao;

    @Autowired
    private CollegeDao collegeDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void testFindAll() {
        assertNotNull(classInfoService.findAll());
    }

    @Test
    void testAddClassInfoSuccess() {
        ClassInfo classInfo = createClassInfo("TEST-CLASS-SERVICE-001", "Service测试班级");

        assertDoesNotThrow(() -> classInfoService.addClassInfo(classInfo));
    }

    @Test
    void testAddClassInfoWithoutCodeShouldFail() {
        ClassInfo classInfo = createClassInfo("TEST-CLASS-SERVICE-002", "无编号班级");
        classInfo.setClassCode(null);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> classInfoService.addClassInfo(classInfo)
        );

        assertEquals("班级编号不能为空", exception.getMessage());
    }

    @Test
    void testAddClassInfoWithoutNameShouldFail() {
        ClassInfo classInfo = createClassInfo("TEST-CLASS-SERVICE-003", "无名称班级");
        classInfo.setClassName(null);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> classInfoService.addClassInfo(classInfo)
        );

        assertEquals("班级名称不能为空", exception.getMessage());
    }

    @Test
    void testAddClassInfoWithoutMajorShouldFail() {
        ClassInfo classInfo = createClassInfo("TEST-CLASS-SERVICE-004", "无专业班级");
        classInfo.setMajorId(null);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> classInfoService.addClassInfo(classInfo)
        );

        assertEquals("所属专业不能为空", exception.getMessage());
    }

    @Test
    void testAddClassInfoWithMissingMajorShouldFail() {
        ClassInfo classInfo = createClassInfo("TEST-CLASS-SERVICE-005", "专业不存在班级");
        classInfo.setMajorId(Long.MAX_VALUE);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> classInfoService.addClassInfo(classInfo)
        );

        assertEquals("所属专业不存在", exception.getMessage());
    }

    @Test
    void testAddClassInfoDuplicateCodeShouldFail() {
        ClassInfo classInfo1 = createClassInfo("TEST-CLASS-SERVICE-006", "第一个班级");
        ClassInfo classInfo2 = createClassInfo("TEST-CLASS-SERVICE-006", "第二个班级");
        classInfoService.addClassInfo(classInfo1);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> classInfoService.addClassInfo(classInfo2)
        );

        assertEquals("班级编号已存在", exception.getMessage());
    }

    @Test
    void testUpdateClassInfoDuplicateCodeShouldFail() {
        ClassInfo classInfo1 = createClassInfo("TEST-CLASS-SERVICE-007", "第一个待修改班级");
        ClassInfo classInfo2 = createClassInfo("TEST-CLASS-SERVICE-008", "第二个待修改班级");
        classInfoService.addClassInfo(classInfo1);
        classInfoService.addClassInfo(classInfo2);

        ClassInfo firstClassInfo = classInfoService.findAll().get(1);
        ClassInfo secondClassInfo = classInfoService.findAll().get(0);
        secondClassInfo.setClassCode(firstClassInfo.getClassCode());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> classInfoService.updateClassInfo(secondClassInfo)
        );

        assertEquals("班级编号已存在", exception.getMessage());
    }

    @Test
    void testDeleteMissingClassInfoShouldFail() {
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> classInfoService.deleteClassInfo(Long.MAX_VALUE)
        );

        assertEquals("班级不存在，无法删除", exception.getMessage());
    }

    @Test
    void testDeleteClassInfoWithStudentShouldFail() {
        ClassInfo classInfo = createClassInfo("TEST-CLASS-SERVICE-009", "有关联学生班级");
        classInfoService.addClassInfo(classInfo);
        ClassInfo inserted = classInfoService.findAll().get(0);

        Long userId = insertStudentUser();
        jdbcTemplate.update(
                """
                        INSERT INTO student(user_id, class_id, student_no, student_name, gender, enrollment_year, status)
                        VALUES (?, ?, ?, ?, ?, ?, 1)
                        """,
                userId,
                inserted.getId(),
                "S" + System.nanoTime(),
                "班级删除校验学生",
                "M",
                2024
        );

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> classInfoService.deleteClassInfo(inserted.getId())
        );

        assertEquals("该班级下仍有学生，不能删除", exception.getMessage());
    }

    @Test
    void testDeleteClassInfoSuccess() {
        ClassInfo classInfo = createClassInfo("TEST-CLASS-SERVICE-010", "逻辑删除班级");
        classInfoService.addClassInfo(classInfo);

        ClassInfo inserted = classInfoService.findAll().get(0);

        assertDoesNotThrow(() -> classInfoService.deleteClassInfo(inserted.getId()));
        assertNull(classInfoDao.findById(inserted.getId()));
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
        college.setDescription("用于班级 Service 测试");
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

    private Long insertStudentUser() {
        String username = "u" + System.nanoTime();
        jdbcTemplate.update(
                "INSERT INTO sys_user(username, password, role, status) VALUES (?, ?, 'STUDENT', 1)",
                username,
                "123456"
        );

        return jdbcTemplate.queryForObject(
                "SELECT id FROM sys_user WHERE username = ?",
                Long.class,
                username
        );
    }
}
