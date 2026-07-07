package com.tzufucius.edu.edumanagementsystem;

import com.tzufucius.edu.edumanagementsystem.dto.request.CollegeRequest;
import com.tzufucius.edu.edumanagementsystem.dto.vo.CollegeVO;
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
        mockMvc.perform(get("/api/colleges").session(TestAuth.adminSession())).andExpect(status().isOk()).andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void getCollege() throws Exception {
        CollegeVO college = createCollege("CTRL-COLLEGE-GET", "Controller Get College");
        mockMvc.perform(get("/api/colleges/{id}", college.id()).session(TestAuth.adminSession()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.collegeCode").value("CTRL-COLLEGE-GET"));
    }

    @Test
    void createCollegeSuccess() throws Exception {
        mockMvc.perform(post("/api/colleges").session(TestAuth.adminSession())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"collegeCode\":\"CTRL-COLLEGE-ADD\",\"collegeName\":\"Controller Add College\",\"description\":\"test\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void updateCollegeSuccess() throws Exception {
        CollegeVO college = createCollege("CTRL-COLLEGE-UPD", "Before");
        mockMvc.perform(put("/api/colleges/{id}", college.id()).session(TestAuth.adminSession())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"collegeCode\":\"CTRL-COLLEGE-UPD\",\"collegeName\":\"After\",\"description\":\"updated\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void deleteCollegeSuccess() throws Exception {
        CollegeVO college = createCollege("CTRL-COLLEGE-DEL", "Delete");
        mockMvc.perform(delete("/api/colleges/{id}", college.id()).session(TestAuth.adminSession()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    private CollegeVO createCollege(String code, String name) {
        collegeService.addCollege(new CollegeRequest(code, name, "test"));
        return collegeService.findAll().stream().filter(item -> code.equals(item.collegeCode())).findFirst().orElseThrow();
    }
}
