package com.tzufucius.edu.edumanagementsystem;

import com.tzufucius.edu.edumanagementsystem.entity.College;
import com.tzufucius.edu.edumanagementsystem.service.CollegeService;
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
class CollegeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CollegeService collegeService;

    @Test
    void listColleges() throws Exception {
        mockMvc.perform(get("/api/colleges"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void getCollege() throws Exception {
        College college = createCollege("CTRL-COLLEGE-GET", "Controller查询学院");

        mockMvc.perform(get("/api/colleges/{id}", college.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.collegeCode").value("CTRL-COLLEGE-GET"));
    }

    @Test
    void createCollegeSuccess() throws Exception {
        mockMvc.perform(post("/api/colleges")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"collegeCode":"CTRL-COLLEGE-ADD","collegeName":"Controller新增学院","description":"测试"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void createCollegeWithoutNameShouldFail() throws Exception {
        mockMvc.perform(post("/api/colleges")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"collegeCode":"CTRL-COLLEGE-NONAME"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("学院名称不能为空"));
    }

    @Test
    void updateCollegeSuccess() throws Exception {
        College college = createCollege("CTRL-COLLEGE-UPD", "Controller修改前学院");

        mockMvc.perform(put("/api/colleges/{id}", college.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"collegeCode":"CTRL-COLLEGE-UPD","collegeName":"Controller修改后学院","description":"已修改"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void deleteCollegeSuccess() throws Exception {
        College college = createCollege("CTRL-COLLEGE-DEL", "Controller删除学院");

        mockMvc.perform(delete("/api/colleges/{id}", college.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    private College createCollege(String code, String name) {
        College college = new College();
        college.setCollegeCode(code);
        college.setCollegeName(name);
        collegeService.addCollege(college);
        return collegeService.findAll().stream()
                .filter(item -> code.equals(item.getCollegeCode()))
                .findFirst()
                .orElseThrow();
    }
}
