package com.tzufucius.edu.edumanagementsystem;

import com.tzufucius.edu.edumanagementsystem.entity.College;
import com.tzufucius.edu.edumanagementsystem.entity.Department;
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
    private CollegeService collegeService;

    @Autowired
    private DepartmentService departmentService;

    @Test
    void listDepartments() throws Exception {
        mockMvc.perform(get("/api/departments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void getDepartment() throws Exception {
        Department department = createDepartment("CTRL-DEPT-GET", "Controller查询教研室");

        mockMvc.perform(get("/api/departments/{id}", department.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.departmentCode").value("CTRL-DEPT-GET"));
    }

    @Test
    void createDepartmentSuccess() throws Exception {
        College college = createCollege("CTRL-DEPT-COLLEGE-ADD");

        mockMvc.perform(post("/api/departments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"collegeId":%d,"departmentCode":"CTRL-DEPT-ADD","departmentName":"Controller新增教研室","officeLocation":"A101"}
                                """.formatted(college.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void createDepartmentWithoutNameShouldFail() throws Exception {
        College college = createCollege("CTRL-DEPT-COLLEGE-NONAME");

        mockMvc.perform(post("/api/departments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"collegeId":%d,"departmentCode":"CTRL-DEPT-NONAME"}
                                """.formatted(college.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("教研室名称不能为空"));
    }

    @Test
    void updateDepartmentSuccess() throws Exception {
        Department department = createDepartment("CTRL-DEPT-UPD", "Controller修改前教研室");

        mockMvc.perform(put("/api/departments/{id}", department.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"collegeId":%d,"departmentCode":"CTRL-DEPT-UPD","departmentName":"Controller修改后教研室","officeLocation":"B202"}
                                """.formatted(department.getCollegeId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void deleteDepartmentSuccess() throws Exception {
        Department department = createDepartment("CTRL-DEPT-DEL", "Controller删除教研室");

        mockMvc.perform(delete("/api/departments/{id}", department.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    private College createCollege(String code) {
        College college = new College();
        college.setCollegeCode(code);
        college.setCollegeName(code + "学院");
        collegeService.addCollege(college);
        return collegeService.findAll().stream()
                .filter(item -> code.equals(item.getCollegeCode()))
                .findFirst()
                .orElseThrow();
    }

    private Department createDepartment(String code, String name) {
        College college = createCollege(code + "-COLLEGE");
        Department department = new Department();
        department.setCollegeId(college.getId());
        department.setDepartmentCode(code);
        department.setDepartmentName(name);
        departmentService.addDepartment(department);
        return departmentService.findAll().stream()
                .filter(item -> code.equals(item.getDepartmentCode()))
                .findFirst()
                .orElseThrow();
    }
}
