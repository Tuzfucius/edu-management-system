package com.tzufucius.edu.edumanagementsystem;

import com.tzufucius.edu.edumanagementsystem.dao.CollegeDao;
import com.tzufucius.edu.edumanagementsystem.dao.DepartmentDao;
import com.tzufucius.edu.edumanagementsystem.entity.College;
import com.tzufucius.edu.edumanagementsystem.entity.Department;
import com.tzufucius.edu.edumanagementsystem.service.DepartmentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class DepartmentServiceTest {

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private DepartmentDao departmentDao;

    @Autowired
    private CollegeDao collegeDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void testFindAll() {
        assertNotNull(departmentService.findAll());
    }

    @Test
    void testAddDepartmentSuccess() {
        Department department = createDepartment("TEST-DEPT-SERVICE-001", "Service测试教研室");

        assertDoesNotThrow(() -> departmentService.addDepartment(department));
    }

    @Test
    void testAddDepartmentWithoutCodeShouldFail() {
        Department department = createDepartment("TEST-DEPT-SERVICE-002", "无编号教研室");
        department.setDepartmentCode(null);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> departmentService.addDepartment(department)
        );

        assertEquals("教研室编号不能为空", exception.getMessage());
    }

    @Test
    void testAddDepartmentWithoutNameShouldFail() {
        Department department = createDepartment("TEST-DEPT-SERVICE-003", "无名称教研室");
        department.setDepartmentName(null);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> departmentService.addDepartment(department)
        );

        assertEquals("教研室名称不能为空", exception.getMessage());
    }

    @Test
    void testAddDepartmentWithoutCollegeShouldFail() {
        Department department = createDepartment("TEST-DEPT-SERVICE-004", "无学院教研室");
        department.setCollegeId(null);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> departmentService.addDepartment(department)
        );

        assertEquals("所属学院不能为空", exception.getMessage());
    }

    @Test
    void testAddDepartmentWithMissingCollegeShouldFail() {
        Department department = createDepartment("TEST-DEPT-SERVICE-005", "学院不存在教研室");
        department.setCollegeId(Long.MAX_VALUE);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> departmentService.addDepartment(department)
        );

        assertEquals("所属学院不存在", exception.getMessage());
    }

    @Test
    void testAddDepartmentDuplicateCodeShouldFail() {
        Department department1 = createDepartment("TEST-DEPT-SERVICE-006", "第一个教研室");
        Department department2 = createDepartment("TEST-DEPT-SERVICE-006", "第二个教研室");
        departmentService.addDepartment(department1);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> departmentService.addDepartment(department2)
        );

        assertEquals("教研室编号已存在", exception.getMessage());
    }

    @Test
    void testUpdateDepartmentDuplicateCodeShouldFail() {
        Department department1 = createDepartment("TEST-DEPT-SERVICE-007", "第一个待修改教研室");
        Department department2 = createDepartment("TEST-DEPT-SERVICE-008", "第二个待修改教研室");
        departmentService.addDepartment(department1);
        departmentService.addDepartment(department2);

        Department firstDepartment = departmentService.findAll().get(1);
        Department secondDepartment = departmentService.findAll().get(0);
        secondDepartment.setDepartmentCode(firstDepartment.getDepartmentCode());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> departmentService.updateDepartment(secondDepartment)
        );

        assertEquals("教研室编号已存在", exception.getMessage());
    }

    @Test
    void testDeleteMissingDepartmentShouldFail() {
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> departmentService.deleteDepartment(Long.MAX_VALUE)
        );

        assertEquals("教研室不存在，无法删除", exception.getMessage());
    }

    @Test
    void testDeleteDepartmentWithTeacherShouldFail() {
        Department department = createDepartment("TEST-DEPT-SERVICE-009", "有关联教师教研室");
        departmentService.addDepartment(department);
        Department inserted = departmentService.findAll().get(0);

        Long userId = insertTeacherUser();
        jdbcTemplate.update(
                """
                        INSERT INTO teacher(user_id, department_id, teacher_no, teacher_name, gender, title, status)
                        VALUES (?, ?, ?, ?, ?, ?, 1)
                        """,
                userId,
                inserted.getId(),
                "T" + System.nanoTime(),
                "教研室删除校验教师",
                "M",
                "讲师"
        );

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> departmentService.deleteDepartment(inserted.getId())
        );

        assertEquals("该教研室下仍有教师，不能删除", exception.getMessage());
    }

    @Test
    void testDeleteDepartmentSuccess() {
        Department department = createDepartment("TEST-DEPT-SERVICE-010", "逻辑删除教研室");
        departmentService.addDepartment(department);

        Department inserted = departmentService.findAll().get(0);

        assertDoesNotThrow(() -> departmentService.deleteDepartment(inserted.getId()));
        assertNull(departmentDao.findById(inserted.getId()));
    }

    private Department createDepartment(String code, String name) {
        College college = new College();
        college.setCollegeCode("C" + System.nanoTime());
        college.setCollegeName(name + "所属学院");
        college.setDescription("用于教研室 Service 测试");
        collegeDao.insert(college);

        Department department = new Department();
        department.setCollegeId(collegeDao.findAll().get(0).getId());
        department.setDepartmentCode(code);
        department.setDepartmentName(name);
        department.setOfficeLocation("测试楼 201");
        return department;
    }

    private Long insertTeacherUser() {
        String username = "u" + System.nanoTime();
        jdbcTemplate.update(
                "INSERT INTO sys_user(username, password, role, status) VALUES (?, ?, 'TEACHER', 1)",
                username,
                "123456"
        );

        return jdbcTemplate.queryForObject(
                "SELECT id FROM sys_user WHERE username = ?",
                Long.class,
                username
        );
    }
}
