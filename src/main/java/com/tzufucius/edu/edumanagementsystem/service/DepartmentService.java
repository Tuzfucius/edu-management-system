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

    public List<DepartmentVO> findAll() {
        return departmentDao.findAll().stream().map(BasicMapper::toVO).toList();
    }

    public DepartmentVO findById(Long id) {
        return BasicMapper.toVO(requireDepartment(id));
    }

    public void addDepartment(DepartmentRequest request) {
        Department department = BasicMapper.toEntity(request);
        checkRequiredFields(department);
        checkCollegeExists(department.getCollegeId());
        if (departmentDao.countByDepartmentCode(department.getDepartmentCode()) > 0) {
            throw new RuntimeException("Department code already exists");
        }
        if (departmentDao.insert(department) != 1) {
            throw new RuntimeException("Failed to create department");
        }
    }

    public void updateDepartment(Long id, DepartmentRequest request) {
        Department department = BasicMapper.toEntity(request);
        department.setId(id);
        checkRequiredFields(department);
        requireDepartment(id);
        checkCollegeExists(department.getCollegeId());
        if (departmentDao.countByDepartmentCodeExcludeId(department.getDepartmentCode(), id) > 0) {
            throw new RuntimeException("Department code already exists");
        }
        if (departmentDao.update(department) != 1) {
            throw new RuntimeException("Failed to update department");
        }
    }

    public void deleteDepartment(Long id) {
        requireDepartment(id);
        if (departmentDao.countTeacherByDepartmentId(id) > 0) {
            throw new RuntimeException("Department still has teachers");
        }
        if (departmentDao.disableById(id) != 1) {
            throw new RuntimeException("Failed to delete department");
        }
    }

    private Department requireDepartment(Long id) {
        if (id == null) {
            throw new RuntimeException("Department id is required");
        }
        Department department = departmentDao.findById(id);
        if (department == null) {
            throw new RuntimeException("Department does not exist");
        }
        return department;
    }

    private void checkCollegeExists(Long collegeId) {
        if (collegeId == null || departmentDao.countCollegeById(collegeId) == 0) {
            throw new RuntimeException("College does not exist");
        }
    }

    private void checkRequiredFields(Department department) {
        if (department == null || department.getDepartmentCode() == null || department.getDepartmentCode().isBlank()
                || department.getDepartmentName() == null || department.getDepartmentName().isBlank()) {
            throw new RuntimeException("Required department fields are missing");
        }
    }
}
