package com.tzufucius.edu.edumanagementsystem;

import com.tzufucius.edu.edumanagementsystem.entity.College;
import com.tzufucius.edu.edumanagementsystem.entity.Major;
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
    private CollegeService collegeService;

    @Autowired
    private MajorService majorService;

    @Test
    void listMajors() throws Exception {
        mockMvc.perform(get("/api/majors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void getMajor() throws Exception {
        Major major = createMajor("CTRL-MAJOR-GET", "Controller查询专业");

        mockMvc.perform(get("/api/majors/{id}", major.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.majorCode").value("CTRL-MAJOR-GET"));
    }

    @Test
    void createMajorSuccess() throws Exception {
        College college = createCollege("CTRL-MAJOR-COLLEGE-ADD");

        mockMvc.perform(post("/api/majors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"collegeId":%d,"majorCode":"CTRL-MAJOR-ADD","majorName":"Controller新增专业","schoolingYears":4,"degreeType":"工学"}
                                """.formatted(college.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void createMajorWithoutNameShouldFail() throws Exception {
        College college = createCollege("CTRL-MAJOR-COLLEGE-NONAME");

        mockMvc.perform(post("/api/majors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"collegeId":%d,"majorCode":"CTRL-MAJOR-NONAME","schoolingYears":4}
                                """.formatted(college.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("专业名称不能为空"));
    }

    @Test
    void updateMajorSuccess() throws Exception {
        Major major = createMajor("CTRL-MAJOR-UPD", "Controller修改前专业");

        mockMvc.perform(put("/api/majors/{id}", major.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"collegeId":%d,"majorCode":"CTRL-MAJOR-UPD","majorName":"Controller修改后专业","schoolingYears":4,"degreeType":"工学"}
                                """.formatted(major.getCollegeId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void deleteMajorSuccess() throws Exception {
        Major major = createMajor("CTRL-MAJOR-DEL", "Controller删除专业");

        mockMvc.perform(delete("/api/majors/{id}", major.getId()))
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

    private Major createMajor(String code, String name) {
        College college = createCollege(code + "-COLLEGE");
        Major major = new Major();
        major.setCollegeId(college.getId());
        major.setMajorCode(code);
        major.setMajorName(name);
        major.setSchoolingYears(4);
        majorService.addMajor(major);
        return majorService.findAll().stream()
                .filter(item -> code.equals(item.getMajorCode()))
                .findFirst()
                .orElseThrow();
    }
}
