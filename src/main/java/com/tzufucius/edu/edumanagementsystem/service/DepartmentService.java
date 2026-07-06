package com.tzufucius.edu.edumanagementsystem.service;

import com.tzufucius.edu.edumanagementsystem.dao.DepartmentDao;
import com.tzufucius.edu.edumanagementsystem.dto.request.DepartmentRequest;
import com.tzufucius.edu.edumanagementsystem.dto.vo.DepartmentVO;
import com.tzufucius.edu.edumanagementsystem.entity.Department;
import com.tzufucius.edu.edumanagementsystem.mapper.BasicMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepartmentService {
    private final DepartmentDao departmentDao;

    public DepartmentService(DepartmentDao departmentDao) {
        this.departmentDao = departmentDao;
    }

    public List<Department> findAll() {
        return departmentDao.findAll();
    }

    public List<DepartmentVO> findAllVO() {
        return findAll().stream().map(BasicMapper::toVO).toList();
    }

    public Department findById(Long id) {
        return requireDepartment(id, "教研室不存在");
    }

    public DepartmentVO findByIdVO(Long id) {
        return BasicMapper.toVO(findById(id));
    }

    public void addDepartment(DepartmentRequest request) {
        addDepartment(BasicMapper.toEntity(request));
    }

    public void addDepartment(Department department) {
        checkRequiredFields(department);
        checkCollegeExists(department.getCollegeId());
        if (departmentDao.countByDepartmentCode(department.getDepartmentCode()) > 0) {
            throw new RuntimeException("教研室编号已存在");
        }
        if (departmentDao.insert(department) != 1) {
            throw new RuntimeException("新增教研室失败");
        }
    }

    public void updateDepartment(Long id, DepartmentRequest request) {
        Department department = BasicMapper.toEntity(request);
        department.setId(id);
        updateDepartment(department);
    }

    public void updateDepartment(Department department) {
        Long id = department.getId();
        checkRequiredFields(department);
        requireDepartment(id, "教研室不存在，无法修改");
        checkCollegeExists(department.getCollegeId());
        if (departmentDao.countByDepartmentCodeExcludeId(department.getDepartmentCode(), id) > 0) {
            throw new RuntimeException("教研室编号已存在");
        }
        if (departmentDao.update(department) != 1) {
            throw new RuntimeException("修改教研室失败");
        }
    }

    public void deleteDepartment(Long id) {
        requireDepartment(id, "教研室不存在，无法删除");
        if (departmentDao.countTeacherByDepartmentId(id) > 0) {
            throw new RuntimeException("该教研室下仍有教师，不能删除");
        }
        if (departmentDao.disableById(id) != 1) {
            throw new RuntimeException("删除教研室失败");
        }
    }

    private Department requireDepartment(Long id, String message) {
        if (id == null) {
            throw new RuntimeException("教研室ID不能为空");
        }
        Department department = departmentDao.findById(id);
        if (department == null) {
            throw new RuntimeException(message);
        }
        return department;
    }

    private void checkCollegeExists(Long collegeId) {
        if (collegeId == null) {
            throw new RuntimeException("所属学院不能为空");
        }
        if (departmentDao.countCollegeById(collegeId) == 0) {
            throw new RuntimeException("所属学院不存在");
        }
    }

    private void checkRequiredFields(Department department) {
        if (department == null) {
            throw new RuntimeException("教研室信息不能为空");
        }
        if (department.getDepartmentCode() == null || department.getDepartmentCode().isBlank()) {
            throw new RuntimeException("教研室编号不能为空");
        }
        if (department.getDepartmentName() == null || department.getDepartmentName().isBlank()) {
            throw new RuntimeException("教研室名称不能为空");
        }
    }
}
