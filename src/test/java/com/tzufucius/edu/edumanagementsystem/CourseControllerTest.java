package com.tzufucius.edu.edumanagementsystem;

import com.tzufucius.edu.edumanagementsystem.dto.request.CourseRequest;
import com.tzufucius.edu.edumanagementsystem.dto.vo.CourseVO;
import com.tzufucius.edu.edumanagementsystem.service.CourseService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CourseControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private CourseService courseService;

    @Test
    void listAndGetCourse() throws Exception {
        CourseVO course = create("COURSE-CTRL-GET");
        mockMvc.perform(get("/api/courses")).andExpect(status().isOk()).andExpect(jsonPath("$.code").value(200));
        mockMvc.perform(get("/api/courses/{id}", course.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.courseCode").value("COURSE-CTRL-GET"));
    }

    @Test
    void createUpdateDeleteCourse() throws Exception {
        mockMvc.perform(post("/api/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"courseCode\":\"COURSE-CTRL-ADD\",\"courseName\":\"Add\",\"credit\":2.5,\"totalHours\":40,\"courseType\":\"required\",\"examType\":\"exam\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
        CourseVO course = courseService.findAll().stream().filter(item -> "COURSE-CTRL-ADD".equals(item.courseCode())).findFirst().orElseThrow();
        mockMvc.perform(put("/api/courses/{id}", course.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"courseCode\":\"COURSE-CTRL-UPD\",\"courseName\":\"Updated\",\"credit\":3,\"totalHours\":48,\"courseType\":\"required\",\"examType\":\"exam\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
        mockMvc.perform(delete("/api/courses/{id}", course.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    private CourseVO create(String code) {
        courseService.addCourse(new CourseRequest(code, code, new BigDecimal("2.5"), 40, "required", "exam"));
        return courseService.findAll().stream().filter(item -> code.equals(item.courseCode())).findFirst().orElseThrow();
    }
}
