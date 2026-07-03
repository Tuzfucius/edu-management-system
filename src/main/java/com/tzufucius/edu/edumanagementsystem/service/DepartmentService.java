package com.tzufucius.edu.edumanagementsystem.service;

import com.tzufucius.edu.edumanagementsystem.dao.CollegeDao;
import com.tzufucius.edu.edumanagementsystem.dao.DepartmentDao;
import com.tzufucius.edu.edumanagementsystem.entity.Department;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepartmentService {

    private final DepartmentDao departmentDao;
    private final CollegeDao collegeDao;

    public DepartmentService(DepartmentDao departmentDao, CollegeDao collegeDao) {
        this.departmentDao = departmentDao;
        this.collegeDao = collegeDao;
    }

    /**
     * 查询全部正常教研室。
     *
     * @return 正常状态的教研室列表
     */
    public List<Department> findAll() {
        return departmentDao.findAll();
    }

    /**
     * 根据教研室ID查询教研室详情。
     *
     * @param id 教研室ID
     * @return 教研室详情
     * @throws RuntimeException 教研室ID为空或教研室不存在时抛出
     */
    public Department findById(Long id) {
        if (id == null) {
            throw new RuntimeException("教研室ID不能为空");
        }

        Department department = departmentDao.findById(id);
        if (department == null) {
            throw new RuntimeException("教研室不存在");
        }

        return department;
    }

    /**
     * 新增教研室，并校验必填字段、所属学院和教研室编号唯一性。
     *
     * @param department 待新增的教研室信息
     * @throws RuntimeException 教研室信息不合法、学院不存在、编号重复或新增失败时抛出
     */
    public void addDepartment(Department department) {
        checkDepartmentForAdd(department);
        checkCollegeExists(department.getCollegeId());

        int exists = departmentDao.countByDepartmentCode(department.getDepartmentCode());
        if (exists > 0) {
            throw new RuntimeException("教研室编号已存在");
        }

        int rows = departmentDao.insert(department);
        if (rows != 1) {
            throw new RuntimeException("新增教研室失败");
        }
    }

    /**
     * 修改教研室，并校验目标教研室、所属学院和教研室编号唯一性。
     *
     * @param department 待修改的教研室信息
     * @throws RuntimeException 教研室信息不合法、教研室不存在、学院不存在、编号重复或修改失败时抛出
     */
    public void updateDepartment(Department department) {
        checkDepartmentForUpdate(department);

        Department oldDepartment = departmentDao.findById(department.getId());
        if (oldDepartment == null) {
            throw new RuntimeException("教研室不存在，无法修改");
        }

        checkCollegeExists(department.getCollegeId());

        int exists = departmentDao.countByDepartmentCodeExcludeId(department.getDepartmentCode(), department.getId());
        if (exists > 0) {
            throw new RuntimeException("教研室编号已存在");
        }

        int rows = departmentDao.update(department);
        if (rows != 1) {
            throw new RuntimeException("修改教研室失败");
        }
    }

    /**
     * 逻辑删除教研室。删除前会校验教研室是否存在，以及教研室下是否仍有正常教师。
     *
     * @param id 教研室ID
     * @throws RuntimeException 教研室ID为空、教研室不存在、仍有关联教师或删除失败时抛出
     */
    public void deleteDepartment(Long id) {
        if (id == null) {
            throw new RuntimeException("教研室ID不能为空");
        }

        Department department = departmentDao.findById(id);
        if (department == null) {
            throw new RuntimeException("教研室不存在，无法删除");
        }

        int teacherCount = departmentDao.countTeacherByDepartmentId(id);
        if (teacherCount > 0) {
            throw new RuntimeException("该教研室下仍有教师，不能删除");
        }

        int rows = departmentDao.disableById(id);
        if (rows != 1) {
            throw new RuntimeException("删除教研室失败");
        }
    }

    /**
     * 校验新增教研室需要的基础字段。
     *
     * @param department 待新增的教研室信息
     * @throws RuntimeException 教研室对象、所属学院、教研室编号或教研室名称为空时抛出
     */
    private void checkDepartmentForAdd(Department department) {
        if (department == null) {
            throw new RuntimeException("教研室信息不能为空");
        }

        checkRequiredFields(department);
    }

    /**
     * 校验修改教研室需要的基础字段。
     *
     * @param department 待修改的教研室信息
     * @throws RuntimeException 教研室对象、教研室ID、所属学院、教研室编号或教研室名称为空时抛出
     */
    private void checkDepartmentForUpdate(Department department) {
        if (department == null) {
            throw new RuntimeException("教研室信息不能为空");
        }

        if (department.getId() == null) {
            throw new RuntimeException("教研室ID不能为空");
        }

        checkRequiredFields(department);
    }

    private void checkRequiredFields(Department department) {
        if (department.getCollegeId() == null) {
            throw new RuntimeException("所属学院不能为空");
        }

        if (department.getDepartmentCode() == null || department.getDepartmentCode().isBlank()) {
            throw new RuntimeException("教研室编号不能为空");
        }

        if (department.getDepartmentName() == null || department.getDepartmentName().isBlank()) {
            throw new RuntimeException("教研室名称不能为空");
        }
    }

    private void checkCollegeExists(Long collegeId) {
        if (collegeDao.findById(collegeId) == null) {
            throw new RuntimeException("所属学院不存在");
        }
    }
}
