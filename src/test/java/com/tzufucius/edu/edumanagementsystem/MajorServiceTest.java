package com.tzufucius.edu.edumanagementsystem;

import com.tzufucius.edu.edumanagementsystem.dao.CollegeDao;
import com.tzufucius.edu.edumanagementsystem.dao.MajorDao;
import com.tzufucius.edu.edumanagementsystem.entity.College;
import com.tzufucius.edu.edumanagementsystem.entity.Major;
import com.tzufucius.edu.edumanagementsystem.service.MajorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MajorServiceTest {

    @Autowired
    private MajorService majorService;

    @Autowired
    private MajorDao majorDao;

    @Autowired
    private CollegeDao collegeDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void testFindAll() {
        assertNotNull(majorService.findAll());
    }

    @Test
    void testAddMajorSuccess() {
        Major major = createMajor("TEST-MAJOR-SERVICE-001", "Service测试专业");

        assertDoesNotThrow(() -> majorService.addMajor(major));
    }

    @Test
    void testAddMajorWithoutCodeShouldFail() {
        Major major = createMajor("TEST-MAJOR-SERVICE-002", "无编号专业");
        major.setMajorCode(null);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> majorService.addMajor(major)
        );

        assertEquals("专业编号不能为空", exception.getMessage());
    }

    @Test
    void testAddMajorWithoutNameShouldFail() {
        Major major = createMajor("TEST-MAJOR-SERVICE-003", "无名称专业");
        major.setMajorName(null);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> majorService.addMajor(major)
        );

        assertEquals("专业名称不能为空", exception.getMessage());
    }

    @Test
    void testAddMajorWithoutCollegeShouldFail() {
        Major major = createMajor("TEST-MAJOR-SERVICE-004", "无学院专业");
        major.setCollegeId(null);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> majorService.addMajor(major)
        );

        assertEquals("所属学院不能为空", exception.getMessage());
    }

    @Test
    void testAddMajorWithMissingCollegeShouldFail() {
        Major major = createMajor("TEST-MAJOR-SERVICE-005", "学院不存在专业");
        major.setCollegeId(Long.MAX_VALUE);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> majorService.addMajor(major)
        );

        assertEquals("所属学院不存在", exception.getMessage());
    }

    @Test
    void testAddMajorDuplicateCodeShouldFail() {
        Major major1 = createMajor("TEST-MAJOR-SERVICE-006", "第一个专业");
        Major major2 = createMajor("TEST-MAJOR-SERVICE-006", "第二个专业");

        majorService.addMajor(major1);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> majorService.addMajor(major2)
        );

        assertEquals("专业编号已存在", exception.getMessage());
    }

    @Test
    void testUpdateMajorDuplicateCodeShouldFail() {
        Major major1 = createMajor("TEST-MAJOR-SERVICE-007", "第一个待修改专业");
        Major major2 = createMajor("TEST-MAJOR-SERVICE-008", "第二个待修改专业");
        majorService.addMajor(major1);
        majorService.addMajor(major2);

        Major firstMajor = majorService.findAll().get(1);
        Major secondMajor = majorService.findAll().get(0);
        secondMajor.setMajorCode(firstMajor.getMajorCode());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> majorService.updateMajor(secondMajor)
        );

        assertEquals("专业编号已存在", exception.getMessage());
    }

    @Test
    void testDeleteMissingMajorShouldFail() {
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> majorService.deleteMajor(Long.MAX_VALUE)
        );

        assertEquals("专业不存在，无法删除", exception.getMessage());
    }

    @Test
    void testDeleteMajorWithClassShouldFail() {
        Major major = createMajor("TEST-MAJOR-SERVICE-009", "有关联班级专业");
        majorService.addMajor(major);
        Major inserted = majorService.findAll().get(0);

        jdbcTemplate.update(
                "INSERT INTO class_info(major_id, class_code, class_name, entrance_year, status) VALUES (?, ?, ?, ?, 1)",
                inserted.getId(),
                "TEST-CLASS-FOR-MAJOR-001",
                "专业删除校验班级",
                2024
        );

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> majorService.deleteMajor(inserted.getId())
        );

        assertEquals("该专业下仍有班级，不能删除", exception.getMessage());
    }

    @Test
    void testDeleteMajorSuccess() {
        Major major = createMajor("TEST-MAJOR-SERVICE-010", "逻辑删除专业");
        majorService.addMajor(major);

        Major inserted = majorService.findAll().get(0);

        assertDoesNotThrow(() -> majorService.deleteMajor(inserted.getId()));
        assertNull(majorDao.findById(inserted.getId()));
    }

    private Major createMajor(String code, String name) {
        College college = new College();
        college.setCollegeCode("C" + System.nanoTime());
        college.setCollegeName(name + "所属学院");
        college.setDescription("用于专业 Service 测试");
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
