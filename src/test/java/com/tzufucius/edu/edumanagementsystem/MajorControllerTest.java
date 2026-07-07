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
class MajorControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private MajorService majorService;
    @Autowired
    private CollegeService collegeService;

    @Test
    void listAndGetMajor() throws Exception {
        Long collegeId = createCollege("MAJ-CTRL-COL").id();
        MajorVO major = create(collegeId, "MAJ-CTRL-GET");
        mockMvc.perform(get("/api/majors").session(TestAuth.adminSession())).andExpect(status().isOk()).andExpect(jsonPath("$.code").value(200));
        mockMvc.perform(get("/api/majors/{id}", major.id()).session(TestAuth.adminSession()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.majorCode").value("MAJ-CTRL-GET"));
    }

    @Test
    void createUpdateDeleteMajor() throws Exception {
        Long collegeId = createCollege("MAJ-CTRL-COL-CRUD").id();
        mockMvc.perform(post("/api/majors").session(TestAuth.adminSession())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"collegeId\":" + collegeId + ",\"majorCode\":\"MAJ-CTRL-ADD\",\"majorName\":\"Add\",\"schoolingYears\":4,\"degreeType\":\"Bachelor\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
        MajorVO major = majorService.findAll().stream().filter(item -> "MAJ-CTRL-ADD".equals(item.majorCode())).findFirst().orElseThrow();
        mockMvc.perform(put("/api/majors/{id}", major.id()).session(TestAuth.adminSession())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"collegeId\":" + collegeId + ",\"majorCode\":\"MAJ-CTRL-UPD\",\"majorName\":\"Updated\",\"schoolingYears\":4,\"degreeType\":\"Bachelor\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
        mockMvc.perform(delete("/api/majors/{id}", major.id()).session(TestAuth.adminSession()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
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
