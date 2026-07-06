package com.tzufucius.edu.edumanagementsystem;

import com.tzufucius.edu.edumanagementsystem.dto.request.ClassInfoRequest;
import com.tzufucius.edu.edumanagementsystem.dto.request.CollegeRequest;
import com.tzufucius.edu.edumanagementsystem.dto.request.MajorRequest;
import com.tzufucius.edu.edumanagementsystem.dto.vo.ClassInfoVO;
import com.tzufucius.edu.edumanagementsystem.dto.vo.CollegeVO;
import com.tzufucius.edu.edumanagementsystem.dto.vo.MajorVO;
import com.tzufucius.edu.edumanagementsystem.service.ClassInfoService;
import com.tzufucius.edu.edumanagementsystem.service.CollegeService;
import com.tzufucius.edu.edumanagementsystem.service.MajorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ClassInfoServiceTest {
    @Autowired
    private ClassInfoService classInfoService;
    @Autowired
    private MajorService majorService;
    @Autowired
    private CollegeService collegeService;

    @Test
    void testCreateAndUpdateClassInfo() {
        Long majorId = createMajor("CLS-SVC-COL", "CLS-SVC-MAJ").id();
        ClassInfoVO classInfo = create(majorId, "CLS-SVC-001");
        classInfoService.updateClassInfo(classInfo.id(), new ClassInfoRequest(majorId, "CLS-SVC-002", "Updated", 2024));
        assertEquals("CLS-SVC-002", classInfoService.findById(classInfo.id()).classCode());
    }

    @Test
    void testDuplicateCodeShouldFail() {
        Long majorId = createMajor("CLS-SVC-COL-DUP", "CLS-SVC-MAJ-DUP").id();
        create(majorId, "CLS-SVC-DUP");
        assertThrows(RuntimeException.class,
                () -> classInfoService.addClassInfo(new ClassInfoRequest(majorId, "CLS-SVC-DUP", "Dup", 2024)));
    }

    private MajorVO createMajor(String collegeCode, String majorCode) {
        collegeService.addCollege(new CollegeRequest(collegeCode, collegeCode, "test"));
        CollegeVO college = collegeService.findAll().stream().filter(item -> collegeCode.equals(item.collegeCode())).findFirst().orElseThrow();
        majorService.addMajor(new MajorRequest(college.id(), majorCode, majorCode, 4, "Bachelor"));
        return majorService.findAll().stream().filter(item -> majorCode.equals(item.majorCode())).findFirst().orElseThrow();
    }

    private ClassInfoVO create(Long majorId, String code) {
        classInfoService.addClassInfo(new ClassInfoRequest(majorId, code, code, 2024));
        return classInfoService.findAll().stream().filter(item -> code.equals(item.classCode())).findFirst().orElseThrow();
    }
}
