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
class ClassInfoControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ClassInfoService classInfoService;
    @Autowired
    private MajorService majorService;
    @Autowired
    private CollegeService collegeService;

    @Test
    void listAndGetClassInfo() throws Exception {
        Long majorId = createMajor("CLS-CTRL-COL", "CLS-CTRL-MAJ").id();
        ClassInfoVO classInfo = create(majorId, "CLS-CTRL-GET");
        mockMvc.perform(get("/api/classes").session(TestAuth.adminSession())).andExpect(status().isOk()).andExpect(jsonPath("$.code").value(200));
        mockMvc.perform(get("/api/classes/{id}", classInfo.id()).session(TestAuth.adminSession()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.classCode").value("CLS-CTRL-GET"));
    }

    @Test
    void createUpdateDeleteClassInfo() throws Exception {
        Long majorId = createMajor("CLS-CTRL-COL-CRUD", "CLS-CTRL-MAJ-CRUD").id();
        mockMvc.perform(post("/api/classes").session(TestAuth.adminSession())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"majorId\":" + majorId + ",\"classCode\":\"CLS-CTRL-ADD\",\"className\":\"Add\",\"entranceYear\":2024}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
        ClassInfoVO classInfo = classInfoService.findAll().stream().filter(item -> "CLS-CTRL-ADD".equals(item.classCode())).findFirst().orElseThrow();
        mockMvc.perform(put("/api/classes/{id}", classInfo.id()).session(TestAuth.adminSession())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"majorId\":" + majorId + ",\"classCode\":\"CLS-CTRL-UPD\",\"className\":\"Updated\",\"entranceYear\":2024}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
        mockMvc.perform(delete("/api/classes/{id}", classInfo.id()).session(TestAuth.adminSession()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
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
