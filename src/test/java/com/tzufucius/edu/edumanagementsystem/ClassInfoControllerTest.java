package com.tzufucius.edu.edumanagementsystem;

import com.tzufucius.edu.edumanagementsystem.entity.ClassInfo;
import com.tzufucius.edu.edumanagementsystem.entity.College;
import com.tzufucius.edu.edumanagementsystem.entity.Major;
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
    private CollegeService collegeService;

    @Autowired
    private MajorService majorService;

    @Autowired
    private ClassInfoService classInfoService;

    @Test
    void listClassInfos() throws Exception {
        mockMvc.perform(get("/api/classes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void getClassInfo() throws Exception {
        ClassInfo classInfo = createClassInfo("CTRL-CLASS-GET", "Controller查询班级");

        mockMvc.perform(get("/api/classes/{id}", classInfo.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.classCode").value("CTRL-CLASS-GET"));
    }

    @Test
    void createClassInfoSuccess() throws Exception {
        Major major = createMajor("CTRL-CLASS-MAJOR-ADD");

        mockMvc.perform(post("/api/classes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"majorId":%d,"classCode":"CTRL-CLASS-ADD","className":"Controller新增班级","entranceYear":2026}
                                """.formatted(major.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void createClassInfoWithoutNameShouldFail() throws Exception {
        Major major = createMajor("CTRL-CLASS-MAJOR-NONAME");

        mockMvc.perform(post("/api/classes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"majorId":%d,"classCode":"CTRL-CLASS-NONAME","entranceYear":2026}
                                """.formatted(major.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("班级名称不能为空"));
    }

    @Test
    void updateClassInfoSuccess() throws Exception {
        ClassInfo classInfo = createClassInfo("CTRL-CLASS-UPD", "Controller修改前班级");

        mockMvc.perform(put("/api/classes/{id}", classInfo.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"majorId":%d,"classCode":"CTRL-CLASS-UPD","className":"Controller修改后班级","entranceYear":2026}
                                """.formatted(classInfo.getMajorId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void deleteClassInfoSuccess() throws Exception {
        ClassInfo classInfo = createClassInfo("CTRL-CLASS-DEL", "Controller删除班级");

        mockMvc.perform(delete("/api/classes/{id}", classInfo.getId()))
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

    private Major createMajor(String code) {
        College college = createCollege("C-" + code);
        Major major = new Major();
        major.setCollegeId(college.getId());
        major.setMajorCode(code);
        major.setMajorName(code + "专业");
        major.setSchoolingYears(4);
        majorService.addMajor(major);
        return majorService.findAll().stream()
                .filter(item -> code.equals(item.getMajorCode()))
                .findFirst()
                .orElseThrow();
    }

    private ClassInfo createClassInfo(String code, String name) {
        Major major = createMajor("M-" + code);
        ClassInfo classInfo = new ClassInfo();
        classInfo.setMajorId(major.getId());
        classInfo.setClassCode(code);
        classInfo.setClassName(name);
        classInfo.setEntranceYear(2026);
        classInfoService.addClassInfo(classInfo);
        return classInfoService.findAll().stream()
                .filter(item -> code.equals(item.getClassCode()))
                .findFirst()
                .orElseThrow();
    }
}
