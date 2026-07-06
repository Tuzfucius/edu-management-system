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
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class DepartmentControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private DepartmentService departmentService;
    @Autowired
    private CollegeService collegeService;

    @Test
    void listAndGetDepartment() throws Exception {
        Long collegeId = createCollege("DEP-CTRL-COL").id();
        DepartmentVO department = create(collegeId, "DEP-CTRL-GET");
        mockMvc.perform(get("/api/departments")).andExpect(status().isOk()).andExpect(jsonPath("$.code").value(200));
        mockMvc.perform(get("/api/departments/{id}", department.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.departmentCode").value("DEP-CTRL-GET"));
    }

    @Test
    void createUpdateDeleteDepartment() throws Exception {
        Long collegeId = createCollege("DEP-CTRL-COL-CRUD").id();
        mockMvc.perform(post("/api/departments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"collegeId\":" + collegeId + ",\"departmentCode\":\"DEP-CTRL-ADD\",\"departmentName\":\"Add\",\"officeLocation\":\"A1\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
        DepartmentVO department = departmentService.findAll().stream().filter(item -> "DEP-CTRL-ADD".equals(item.departmentCode())).findFirst().orElseThrow();
        mockMvc.perform(put("/api/departments/{id}", department.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"collegeId\":" + collegeId + ",\"departmentCode\":\"DEP-CTRL-UPD\",\"departmentName\":\"Updated\",\"officeLocation\":\"A2\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
        mockMvc.perform(delete("/api/departments/{id}", department.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
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
