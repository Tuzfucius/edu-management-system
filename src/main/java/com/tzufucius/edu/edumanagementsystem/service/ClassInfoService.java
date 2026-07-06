package com.tzufucius.edu.edumanagementsystem.service;

import com.tzufucius.edu.edumanagementsystem.dao.ClassInfoDao;
import com.tzufucius.edu.edumanagementsystem.dto.request.BasicRequests.ClassInfoRequest;
import com.tzufucius.edu.edumanagementsystem.dto.vo.BasicVOs.ClassInfoVO;
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
        return BasicMapper.toVO(requireClassInfo(id, "班级不存在"));
    }

    public void addClassInfo(ClassInfoRequest request) {
        ClassInfo classInfo = BasicMapper.toEntity(request);
        checkMajorExists(classInfo.getMajorId());
        if (classInfoDao.countByClassCode(classInfo.getClassCode()) > 0) {
            throw new RuntimeException("班级编号已存在");
        }
        if (classInfoDao.insert(classInfo) != 1) {
            throw new RuntimeException("新增班级失败");
        }
    }

    public void updateClassInfo(Long id, ClassInfoRequest request) {
        requireClassInfo(id, "班级不存在，无法修改");
        ClassInfo classInfo = BasicMapper.toEntity(request);
        classInfo.setId(id);
        checkMajorExists(classInfo.getMajorId());
        if (classInfoDao.countByClassCodeExcludeId(classInfo.getClassCode(), id) > 0) {
            throw new RuntimeException("班级编号已存在");
        }
        if (classInfoDao.update(classInfo) != 1) {
            throw new RuntimeException("修改班级失败");
        }
    }

    public void deleteClassInfo(Long id) {
        requireClassInfo(id, "班级不存在，无法删除");
        if (classInfoDao.countStudentByClassId(id) > 0) {
            throw new RuntimeException("该班级下仍有学生，不能删除");
        }
        if (classInfoDao.disableById(id) != 1) {
            throw new RuntimeException("删除班级失败");
        }
    }

    private ClassInfo requireClassInfo(Long id, String message) {
        if (id == null) {
            throw new RuntimeException("班级ID不能为空");
        }
        ClassInfo classInfo = classInfoDao.findById(id);
        if (classInfo == null) {
            throw new RuntimeException(message);
        }
        return classInfo;
    }

    private void checkMajorExists(Long majorId) {
        if (classInfoDao.countMajorById(majorId) == 0) {
            throw new RuntimeException("所属专业不存在");
        }
    }
}
