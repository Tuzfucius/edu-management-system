package com.tzufucius.edu.edumanagementsystem;

import com.tzufucius.edu.edumanagementsystem.entity.College;
import com.tzufucius.edu.edumanagementsystem.service.CollegeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class CollegeServiceTest {

    @Autowired
    private CollegeService collegeService;

    @Test
    void testFindAll() {
        assertNotNull(collegeService.findAll());
    }

    @Test
    void testAddCollegeSuccess() {
        College college = new College();
        college.setCollegeCode("TEST-SERVICE-001");
        college.setCollegeName("Service测试学院");
        college.setDescription("用于测试 Service 新增");

        assertDoesNotThrow(() -> collegeService.addCollege(college));
    }

    @Test
    void testAddCollegeWithoutCodeShouldFail() {
        College college = new College();
        college.setCollegeName("无编号学院");

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> collegeService.addCollege(college)
        );

        assertEquals("学院编号不能为空", exception.getMessage());
    }

    @Test
    void testAddCollegeWithoutNameShouldFail() {
        College college = new College();
        college.setCollegeCode("TEST-SERVICE-002");

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> collegeService.addCollege(college)
        );

        assertEquals("学院名称不能为空", exception.getMessage());
    }

    @Test
    void testAddCollegeDuplicateCodeShouldFail() {
        College college1 = new College();
        college1.setCollegeCode("TEST-SERVICE-003");
        college1.setCollegeName("第一个学院");

        College college2 = new College();
        college2.setCollegeCode("TEST-SERVICE-003");
        college2.setCollegeName("第二个学院");

        collegeService.addCollege(college1);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> collegeService.addCollege(college2)
        );

        assertEquals("学院编号已存在", exception.getMessage());
    }

    @Test
    void testUpdateCollegeDuplicateCodeShouldFail() {
        College college1 = new College();
        college1.setCollegeCode("TEST-SERVICE-004");
        college1.setCollegeName("第一个待修改学院");

        College college2 = new College();
        college2.setCollegeCode("TEST-SERVICE-005");
        college2.setCollegeName("第二个待修改学院");

        collegeService.addCollege(college1);
        collegeService.addCollege(college2);

        College firstCollege = collegeService.findAll().get(1);
        College secondCollege = collegeService.findAll().get(0);

        secondCollege.setCollegeCode(firstCollege.getCollegeCode());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> collegeService.updateCollege(secondCollege)
        );

        assertEquals("学院编号已存在", exception.getMessage());
    }
}
