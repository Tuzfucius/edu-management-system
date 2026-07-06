package com.tzufucius.edu.edumanagementsystem;

import com.tzufucius.edu.edumanagementsystem.dto.request.CourseRequest;
import com.tzufucius.edu.edumanagementsystem.dto.vo.CourseVO;
import com.tzufucius.edu.edumanagementsystem.service.CourseService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class CourseServiceTest {
    @Autowired
    private CourseService courseService;

    @Test
    void testCreateAndUpdateCourse() {
        CourseVO course = create("COURSE-SVC-001");
        courseService.updateCourse(course.id(), new CourseRequest("COURSE-SVC-002", "Updated", new BigDecimal("3.0"), 48, "required", "exam"));
        assertEquals("COURSE-SVC-002", courseService.findById(course.id()).courseCode());
    }

    @Test
    void testDuplicateCodeShouldFail() {
        create("COURSE-SVC-DUP");
        RuntimeException exception = assertThrows(RuntimeException.class, () -> courseService.addCourse(request("COURSE-SVC-DUP")));
        assertEquals("Course code already exists", exception.getMessage());
    }

    @Test
    void testInvalidCreditShouldFail() {
        assertThrows(RuntimeException.class,
                () -> courseService.addCourse(new CourseRequest("COURSE-SVC-BAD", "Bad", BigDecimal.ZERO, 48, "required", "exam")));
    }

    private CourseVO create(String code) {
        courseService.addCourse(request(code));
        return courseService.findAll().stream().filter(item -> code.equals(item.courseCode())).findFirst().orElseThrow();
    }

    private CourseRequest request(String code) {
        return new CourseRequest(code, code, new BigDecimal("2.5"), 40, "required", "exam");
    }
}
