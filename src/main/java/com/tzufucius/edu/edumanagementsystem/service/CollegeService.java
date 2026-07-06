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

    public List<CollegeVO> findAll() {
        return collegeDao.findAll().stream().map(BasicMapper::toVO).toList();
    }

    public CollegeVO findById(Long id) {
        return BasicMapper.toVO(requireCollege(id, "学院不存在"));
    }

    public void addCollege(CollegeRequest request) {
        College college = BasicMapper.toEntity(request);
        if (collegeDao.countByCollegeCode(college.getCollegeCode()) > 0) {
            throw new RuntimeException("学院编号已存在");
        }
        if (collegeDao.insert(college) != 1) {
            throw new RuntimeException("新增学院失败");
        }
    }

    public void updateCollege(Long id, CollegeRequest request) {
        requireCollege(id, "学院不存在，无法修改");
        College college = BasicMapper.toEntity(request);
        college.setId(id);
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
}
