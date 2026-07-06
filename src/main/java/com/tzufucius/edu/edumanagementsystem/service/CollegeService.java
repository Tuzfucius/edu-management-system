package com.tzufucius.edu.edumanagementsystem.service;

import com.tzufucius.edu.edumanagementsystem.dao.CollegeDao;
import com.tzufucius.edu.edumanagementsystem.dto.request.CollegeRequest;
import com.tzufucius.edu.edumanagementsystem.dto.vo.CollegeVO;
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

    public List<CollegeVO> findAll() {
        return collegeDao.findAll().stream().map(BasicMapper::toVO).toList();
    }

    public CollegeVO findById(Long id) {
        return BasicMapper.toVO(requireCollege(id));
    }

    public void addCollege(CollegeRequest request) {
        College college = BasicMapper.toEntity(request);
        checkRequiredFields(college);
        if (collegeDao.countByCollegeCode(college.getCollegeCode()) > 0) {
            throw new RuntimeException("College code already exists");
        }
        if (collegeDao.insert(college) != 1) {
            throw new RuntimeException("Failed to create college");
        }
    }

    public void updateCollege(Long id, CollegeRequest request) {
        College college = BasicMapper.toEntity(request);
        college.setId(id);
        checkRequiredFields(college);
        requireCollege(id);
        if (collegeDao.countByCollegeCodeExcludeId(college.getCollegeCode(), id) > 0) {
            throw new RuntimeException("College code already exists");
        }
        if (collegeDao.update(college) != 1) {
            throw new RuntimeException("Failed to update college");
        }
    }

    public void deleteCollege(Long id) {
        requireCollege(id);
        if (collegeDao.countMajorByCollegeId(id) > 0) {
            throw new RuntimeException("College still has majors");
        }
        if (collegeDao.disableById(id) != 1) {
            throw new RuntimeException("Failed to delete college");
        }
    }

    private College requireCollege(Long id) {
        if (id == null) {
            throw new RuntimeException("College id is required");
        }
        College college = collegeDao.findById(id);
        if (college == null) {
            throw new RuntimeException("College does not exist");
        }
        return college;
    }

    private void checkRequiredFields(College college) {
        if (college == null || college.getCollegeCode() == null || college.getCollegeCode().isBlank()
                || college.getCollegeName() == null || college.getCollegeName().isBlank()) {
            throw new RuntimeException("Required college fields are missing");
        }
    }
}
