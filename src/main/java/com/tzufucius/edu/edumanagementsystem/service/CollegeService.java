package com.tzufucius.edu.edumanagementsystem.service;

import com.tzufucius.edu.edumanagementsystem.dao.CollegeDao;
import com.tzufucius.edu.edumanagementsystem.dto.request.BasicRequests.CollegeRequest;
import com.tzufucius.edu.edumanagementsystem.dto.vo.BasicVOs.CollegeVO;
import com.tzufucius.edu.edumanagementsystem.entity.College;
import com.tzufucius.edu.edumanagementsystem.mapper.BasicMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CollegeService {
    private final CollegeDao collegeDao;

    public CollegeService(CollegeDao collegeDao) {
        this.collegeDao = collegeDao;
    }

    public List<College> findAll() {
        return collegeDao.findAll();
    }

    public List<CollegeVO> findAllVO() {
        return findAll().stream().map(BasicMapper::toVO).toList();
    }

    public College findById(Long id) {
        return requireCollege(id, "学院不存在");
    }

    public CollegeVO findByIdVO(Long id) {
        return BasicMapper.toVO(findById(id));
    }

    public void addCollege(CollegeRequest request) {
        addCollege(BasicMapper.toEntity(request));
    }

    public void addCollege(College college) {
        checkRequiredFields(college);
        if (collegeDao.countByCollegeCode(college.getCollegeCode()) > 0) {
            throw new RuntimeException("学院编号已存在");
        }
        if (collegeDao.insert(college) != 1) {
            throw new RuntimeException("新增学院失败");
        }
    }

    public void updateCollege(Long id, CollegeRequest request) {
        College college = BasicMapper.toEntity(request);
        college.setId(id);
        updateCollege(college);
    }

    public void updateCollege(College college) {
        Long id = college.getId();
        checkRequiredFields(college);
        requireCollege(id, "学院不存在，无法修改");
        if (collegeDao.countByCollegeCodeExcludeId(college.getCollegeCode(), id) > 0) {
            throw new RuntimeException("学院编号已存在");
        }
        if (collegeDao.update(college) != 1) {
            throw new RuntimeException("修改学院失败");
        }
    }

    public void deleteCollege(Long id) {
        requireCollege(id, "学院不存在，无法删除");
        if (collegeDao.countMajorByCollegeId(id) > 0) {
            throw new RuntimeException("该学院下仍有专业，不能删除");
        }
        if (collegeDao.disableById(id) != 1) {
            throw new RuntimeException("删除学院失败");
        }
    }

    private College requireCollege(Long id, String message) {
        if (id == null) {
            throw new RuntimeException("学院ID不能为空");
        }
        College college = collegeDao.findById(id);
        if (college == null) {
            throw new RuntimeException(message);
        }
        return college;
    }

    private void checkRequiredFields(College college) {
        if (college == null) {
            throw new RuntimeException("学院信息不能为空");
        }
        if (college.getCollegeCode() == null || college.getCollegeCode().isBlank()) {
            throw new RuntimeException("学院编号不能为空");
        }
        if (college.getCollegeName() == null || college.getCollegeName().isBlank()) {
            throw new RuntimeException("学院名称不能为空");
        }
    }
}
