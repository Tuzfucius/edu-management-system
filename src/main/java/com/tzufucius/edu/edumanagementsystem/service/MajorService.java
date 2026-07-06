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

    public List<Major> findAll() {
        return majorDao.findAll();
    }

    public List<MajorVO> findAllVO() {
        return findAll().stream().map(BasicMapper::toVO).toList();
    }

    public Major findById(Long id) {
        return requireMajor(id, "专业不存在");
    }

    public MajorVO findByIdVO(Long id) {
        return BasicMapper.toVO(findById(id));
    }

    public void addMajor(MajorRequest request) {
        addMajor(BasicMapper.toEntity(request));
    }

    public void addMajor(Major major) {
        checkRequiredFields(major);
        checkCollegeExists(major.getCollegeId());
        if (majorDao.countByMajorCode(major.getMajorCode()) > 0) {
            throw new RuntimeException("专业编号已存在");
        }
        if (majorDao.insert(major) != 1) {
            throw new RuntimeException("新增专业失败");
        }
    }

    public void updateMajor(Long id, MajorRequest request) {
        Major major = BasicMapper.toEntity(request);
        major.setId(id);
        updateMajor(major);
    }

    public void updateMajor(Major major) {
        Long id = major.getId();
        checkRequiredFields(major);
        requireMajor(id, "专业不存在，无法修改");
        checkCollegeExists(major.getCollegeId());
        if (majorDao.countByMajorCodeExcludeId(major.getMajorCode(), id) > 0) {
            throw new RuntimeException("专业编号已存在");
        }
        if (majorDao.update(major) != 1) {
            throw new RuntimeException("修改专业失败");
        }
    }

    public void deleteMajor(Long id) {
        requireMajor(id, "专业不存在，无法删除");
        if (majorDao.countClassByMajorId(id) > 0) {
            throw new RuntimeException("该专业下仍有班级，不能删除");
        }
        if (majorDao.disableById(id) != 1) {
            throw new RuntimeException("删除专业失败");
        }
    }

    private Major requireMajor(Long id, String message) {
        if (id == null) {
            throw new RuntimeException("专业ID不能为空");
        }
        Major major = majorDao.findById(id);
        if (major == null) {
            throw new RuntimeException(message);
        }
        return major;
    }

    private void checkCollegeExists(Long collegeId) {
        if (collegeId == null) {
            throw new RuntimeException("所属学院不能为空");
        }
        if (majorDao.countCollegeById(collegeId) == 0) {
            throw new RuntimeException("所属学院不存在");
        }
    }

    private void checkRequiredFields(Major major) {
        if (major == null) {
            throw new RuntimeException("专业信息不能为空");
        }
        if (major.getMajorCode() == null || major.getMajorCode().isBlank()) {
            throw new RuntimeException("专业编号不能为空");
        }
        if (major.getMajorName() == null || major.getMajorName().isBlank()) {
            throw new RuntimeException("专业名称不能为空");
        }
    }
}
