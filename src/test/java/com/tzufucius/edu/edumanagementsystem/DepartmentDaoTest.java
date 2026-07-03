package com.tzufucius.edu.edumanagementsystem;

import com.tzufucius.edu.edumanagementsystem.dao.CollegeDao;
import com.tzufucius.edu.edumanagementsystem.dao.DepartmentDao;
import com.tzufucius.edu.edumanagementsystem.entity.College;
import com.tzufucius.edu.edumanagementsystem.entity.Department;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class DepartmentDaoTest {

    @Autowired
    private DepartmentDao departmentDao;

    @Autowired
    private CollegeDao collegeDao;

    @Test
    void testFindAll() {
        List<Department> departments = departmentDao.findAll();

        assertNotNull(departments);
    }

    @Test
    void testInsert() {
        Department department = createDepartment("TEST-DEPT-DAO-001", "DAO测试教研室");

        int rows = departmentDao.insert(department);

        assertEquals(1, rows);
    }

    @Test
    void testFindById() {
        Department department = createDepartment("TEST-DEPT-DAO-002", "DAO查询测试教研室");
        departmentDao.insert(department);

        Department inserted = departmentDao.findAll().get(0);
        Department result = departmentDao.findById(inserted.getId());

        assertNotNull(result);
        assertEquals(inserted.getId(), result.getId());
    }

    @Test
    void testCountByDepartmentCode() {
        Department department = createDepartment("TEST-DEPT-DAO-003", "DAO编号统计测试教研室");
        departmentDao.insert(department);

        int count = departmentDao.countByDepartmentCode("TEST-DEPT-DAO-003");

        assertEquals(1, count);
    }

    @Test
    void testCountByDepartmentCodeExcludeId() {
        Department department = createDepartment("TEST-DEPT-DAO-004", "DAO排除自身编号测试教研室");
        departmentDao.insert(department);

        Department inserted = departmentDao.findAll().get(0);
        int count = departmentDao.countByDepartmentCodeExcludeId("TEST-DEPT-DAO-004", inserted.getId());

        assertEquals(0, count);
    }

    @Test
    void testDisableById() {
        Department department = createDepartment("TEST-DEPT-DAO-005", "DAO逻辑删除测试教研室");
        departmentDao.insert(department);

        Department inserted = departmentDao.findAll().get(0);
        int rows = departmentDao.disableById(inserted.getId());

        assertEquals(1, rows);
    }

    @Test
    void testCountTeacherByDepartmentId() {
        Department department = createDepartment("TEST-DEPT-DAO-006", "DAO教师数量测试教研室");
        departmentDao.insert(department);

        Department inserted = departmentDao.findAll().get(0);
        int count = departmentDao.countTeacherByDepartmentId(inserted.getId());

        assertEquals(0, count);
    }

    private Department createDepartment(String code, String name) {
        College college = new College();
        college.setCollegeCode("C" + System.nanoTime());
        college.setCollegeName(name + "所属学院");
        college.setDescription("用于教研室 DAO 测试");
        collegeDao.insert(college);

        Department department = new Department();
        department.setCollegeId(collegeDao.findAll().get(0).getId());
        department.setDepartmentCode(code);
        department.setDepartmentName(name);
        department.setOfficeLocation("测试楼 101");
        return department;
    }
}
