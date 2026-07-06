package com.tzufucius.edu.edumanagementsystem;

import com.tzufucius.edu.edumanagementsystem.dto.request.CollegeRequest;
import com.tzufucius.edu.edumanagementsystem.dto.request.DepartmentRequest;
import com.tzufucius.edu.edumanagementsystem.dto.vo.CollegeVO;
import com.tzufucius.edu.edumanagementsystem.dto.vo.DepartmentVO;
import com.tzufucius.edu.edumanagementsystem.service.CollegeService;
import com.tzufucius.edu.edumanagementsystem.service.DepartmentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class DepartmentServiceTest {
    @Autowired
    private DepartmentService departmentService;
    @Autowired
    private CollegeService collegeService;

    @Test
    void testCrudBoundaries() {
        Long collegeId = createCollege("DEP-SVC-COL").id();
        DepartmentVO department = create(collegeId, "DEP-SVC-001");
        assertEquals("DEP-SVC-001", department.departmentCode());
        assertNotNull(departmentService.findById(department.id()));

        departmentService.updateDepartment(department.id(), new DepartmentRequest(collegeId, "DEP-SVC-002", "Updated", "A1"));
        assertEquals("DEP-SVC-002", departmentService.findById(department.id()).departmentCode());
    }

    @Test
    void testDuplicateCodeShouldFail() {
        Long collegeId = createCollege("DEP-SVC-COL-DUP").id();
        create(collegeId, "DEP-SVC-DUP");
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> departmentService.addDepartment(new DepartmentRequest(collegeId, "DEP-SVC-DUP", "Dup", "A1")));
        assertEquals("Department code already exists", exception.getMessage());
    }

    private CollegeVO createCollege(String code) {
        collegeService.addCollege(new CollegeRequest(code, code, "test"));
        return collegeService.findAll().stream().filter(item -> code.equals(item.collegeCode())).findFirst().orElseThrow();
    }

    private DepartmentVO create(Long collegeId, String code) {
        departmentService.addDepartment(new DepartmentRequest(collegeId, code, code, "A1"));
        return departmentService.findAll().stream().filter(item -> code.equals(item.departmentCode())).findFirst().orElseThrow();
    }
}
