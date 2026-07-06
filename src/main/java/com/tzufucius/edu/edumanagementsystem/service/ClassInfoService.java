package com.tzufucius.edu.edumanagementsystem.service;

import com.tzufucius.edu.edumanagementsystem.dao.ClassInfoDao;
import com.tzufucius.edu.edumanagementsystem.dto.request.ClassInfoRequest;
import com.tzufucius.edu.edumanagementsystem.dto.vo.ClassInfoVO;
import com.tzufucius.edu.edumanagementsystem.entity.ClassInfo;
import com.tzufucius.edu.edumanagementsystem.mapper.BasicMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClassInfoService {
    private final ClassInfoDao classInfoDao;

    public ClassInfoService(ClassInfoDao classInfoDao) {
        this.classInfoDao = classInfoDao;
    }

    public List<ClassInfoVO> findAll() {
        return classInfoDao.findAll().stream().map(BasicMapper::toVO).toList();
    }

    public ClassInfoVO findById(Long id) {
        return BasicMapper.toVO(requireClassInfo(id));
    }

    public void addClassInfo(ClassInfoRequest request) {
        ClassInfo classInfo = BasicMapper.toEntity(request);
        checkRequiredFields(classInfo);
        checkMajorExists(classInfo.getMajorId());
        if (classInfoDao.countByClassCode(classInfo.getClassCode()) > 0) {
            throw new RuntimeException("Class code already exists");
        }
        if (classInfoDao.insert(classInfo) != 1) {
            throw new RuntimeException("Failed to create class");
        }
    }

    public void updateClassInfo(Long id, ClassInfoRequest request) {
        ClassInfo classInfo = BasicMapper.toEntity(request);
        classInfo.setId(id);
        checkRequiredFields(classInfo);
        requireClassInfo(id);
        checkMajorExists(classInfo.getMajorId());
        if (classInfoDao.countByClassCodeExcludeId(classInfo.getClassCode(), id) > 0) {
            throw new RuntimeException("Class code already exists");
        }
        if (classInfoDao.update(classInfo) != 1) {
            throw new RuntimeException("Failed to update class");
        }
    }

    public void deleteClassInfo(Long id) {
        requireClassInfo(id);
        if (classInfoDao.countStudentByClassId(id) > 0) {
            throw new RuntimeException("Class still has students");
        }
        if (classInfoDao.disableById(id) != 1) {
            throw new RuntimeException("Failed to delete class");
        }
    }

    private ClassInfo requireClassInfo(Long id) {
        if (id == null) {
            throw new RuntimeException("Class id is required");
        }
        ClassInfo classInfo = classInfoDao.findById(id);
        if (classInfo == null) {
            throw new RuntimeException("Class does not exist");
        }
        return classInfo;
    }

    private void checkMajorExists(Long majorId) {
        if (majorId == null || classInfoDao.countMajorById(majorId) == 0) {
            throw new RuntimeException("Major does not exist");
        }
    }

    private void checkRequiredFields(ClassInfo classInfo) {
        if (classInfo == null || classInfo.getClassCode() == null || classInfo.getClassCode().isBlank()
                || classInfo.getClassName() == null || classInfo.getClassName().isBlank()) {
            throw new RuntimeException("Required class fields are missing");
        }
    }
}
