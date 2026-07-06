package com.tzufucius.edu.edumanagementsystem;

import com.tzufucius.edu.edumanagementsystem.dto.request.CollegeRequest;
import com.tzufucius.edu.edumanagementsystem.dto.request.MajorRequest;
import com.tzufucius.edu.edumanagementsystem.dto.vo.CollegeVO;
import com.tzufucius.edu.edumanagementsystem.dto.vo.MajorVO;
import com.tzufucius.edu.edumanagementsystem.service.CollegeService;
import com.tzufucius.edu.edumanagementsystem.service.MajorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MajorServiceTest {
    @Autowired
    private MajorService majorService;
    @Autowired
    private CollegeService collegeService;

    @Test
    void testCreateAndUpdateMajor() {
        Long collegeId = createCollege("MAJ-SVC-COL").id();
        MajorVO major = create(collegeId, "MAJ-SVC-001");
        majorService.updateMajor(major.id(), new MajorRequest(collegeId, "MAJ-SVC-002", "Updated", 4, "Bachelor"));
        assertEquals("MAJ-SVC-002", majorService.findById(major.id()).majorCode());
    }

    @Test
    void testDuplicateCodeShouldFail() {
        Long collegeId = createCollege("MAJ-SVC-COL-DUP").id();
        create(collegeId, "MAJ-SVC-DUP");
        assertThrows(RuntimeException.class,
                () -> majorService.addMajor(new MajorRequest(collegeId, "MAJ-SVC-DUP", "Dup", 4, "Bachelor")));
    }

    private CollegeVO createCollege(String code) {
        collegeService.addCollege(new CollegeRequest(code, code, "test"));
        return collegeService.findAll().stream().filter(item -> code.equals(item.collegeCode())).findFirst().orElseThrow();
    }

    private MajorVO create(Long collegeId, String code) {
        majorService.addMajor(new MajorRequest(collegeId, code, code, 4, "Bachelor"));
        return majorService.findAll().stream().filter(item -> code.equals(item.majorCode())).findFirst().orElseThrow();
    }
}
