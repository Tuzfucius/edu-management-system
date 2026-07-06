package com.tzufucius.edu.edumanagementsystem.service;

import com.tzufucius.edu.edumanagementsystem.dao.MajorDao;
import com.tzufucius.edu.edumanagementsystem.dto.request.MajorRequest;
import com.tzufucius.edu.edumanagementsystem.dto.vo.MajorVO;
import com.tzufucius.edu.edumanagementsystem.entity.Major;
import com.tzufucius.edu.edumanagementsystem.mapper.BasicMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MajorService {
    private final MajorDao majorDao;

    public MajorService(MajorDao majorDao) {
        this.majorDao = majorDao;
    }

    public List<MajorVO> findAll() {
        return majorDao.findAll().stream().map(BasicMapper::toVO).toList();
    }

    public MajorVO findById(Long id) {
        return BasicMapper.toVO(requireMajor(id));
    }

    public void addMajor(MajorRequest request) {
        Major major = BasicMapper.toEntity(request);
        checkRequiredFields(major);
        checkCollegeExists(major.getCollegeId());
        if (majorDao.countByMajorCode(major.getMajorCode()) > 0) {
            throw new RuntimeException("Major code already exists");
        }
        if (majorDao.insert(major) != 1) {
            throw new RuntimeException("Failed to create major");
        }
    }

    public void updateMajor(Long id, MajorRequest request) {
        Major major = BasicMapper.toEntity(request);
        major.setId(id);
        checkRequiredFields(major);
        requireMajor(id);
        checkCollegeExists(major.getCollegeId());
        if (majorDao.countByMajorCodeExcludeId(major.getMajorCode(), id) > 0) {
            throw new RuntimeException("Major code already exists");
        }
        if (majorDao.update(major) != 1) {
            throw new RuntimeException("Failed to update major");
        }
    }

    public void deleteMajor(Long id) {
        requireMajor(id);
        if (majorDao.countClassByMajorId(id) > 0) {
            throw new RuntimeException("Major still has classes");
        }
        if (majorDao.disableById(id) != 1) {
            throw new RuntimeException("Failed to delete major");
        }
    }

    private Major requireMajor(Long id) {
        if (id == null) {
            throw new RuntimeException("Major id is required");
        }
        Major major = majorDao.findById(id);
        if (major == null) {
            throw new RuntimeException("Major does not exist");
        }
        return major;
    }

    private void checkCollegeExists(Long collegeId) {
        if (collegeId == null || majorDao.countCollegeById(collegeId) == 0) {
            throw new RuntimeException("College does not exist");
        }
    }

    private void checkRequiredFields(Major major) {
        if (major == null || major.getMajorCode() == null || major.getMajorCode().isBlank()
                || major.getMajorName() == null || major.getMajorName().isBlank()) {
            throw new RuntimeException("Required major fields are missing");
        }
    }
}
