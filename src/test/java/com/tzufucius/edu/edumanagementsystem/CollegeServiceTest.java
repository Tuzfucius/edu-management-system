package com.tzufucius.edu.edumanagementsystem;

import com.tzufucius.edu.edumanagementsystem.dto.request.CollegeRequest;
import com.tzufucius.edu.edumanagementsystem.dto.vo.CollegeVO;
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
        assertDoesNotThrow(() -> collegeService.addCollege(request("TEST-SERVICE-001", "Service College")));
    }

    @Test
    void testAddCollegeMissingFieldsShouldFail() {
        assertThrows(RuntimeException.class, () -> collegeService.addCollege(request("", "Name")));
        assertThrows(RuntimeException.class, () -> collegeService.addCollege(request("TEST-SERVICE-002", "")));
    }

    @Test
    void testDuplicateCodeShouldFail() {
        collegeService.addCollege(request("TEST-SERVICE-003", "First"));
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> collegeService.addCollege(request("TEST-SERVICE-003", "Second")));
        assertEquals("College code already exists", exception.getMessage());
    }

    @Test
    void testUpdateDuplicateCodeShouldFail() {
        CollegeVO first = create("TEST-SERVICE-004", "First");
        CollegeVO second = create("TEST-SERVICE-005", "Second");
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> collegeService.updateCollege(second.id(), request(first.collegeCode(), "Second Updated")));
        assertEquals("College code already exists", exception.getMessage());
    }

    private CollegeVO create(String code, String name) {
        collegeService.addCollege(request(code, name));
        return collegeService.findAll().stream().filter(item -> code.equals(item.collegeCode())).findFirst().orElseThrow();
    }

    private CollegeRequest request(String code, String name) {
        return new CollegeRequest(code, name, "test");
    }
}
