package com.tzufucius.edu.edumanagementsystem.service;

import com.tzufucius.edu.edumanagementsystem.dao.MajorDao;
import com.tzufucius.edu.edumanagementsystem.dto.request.BasicRequests.MajorRequest;
import com.tzufucius.edu.edumanagementsystem.dto.vo.BasicVOs.MajorVO;
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
        return BasicMapper.toVO(requireMajor(id, "专业不存在"));
    }

    public void addMajor(MajorRequest request) {
        Major major = BasicMapper.toEntity(request);
        checkCollegeExists(major.getCollegeId());
        if (majorDao.countByMajorCode(major.getMajorCode()) > 0) {
            throw new RuntimeException("专业编号已存在");
        }
        if (majorDao.insert(major) != 1) {
            throw new RuntimeException("新增专业失败");
        }
    }

    public void updateMajor(Long id, MajorRequest request) {
        requireMajor(id, "专业不存在，无法修改");
        Major major = BasicMapper.toEntity(request);
        major.setId(id);
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
        if (majorDao.countCollegeById(collegeId) == 0) {
            throw new RuntimeException("所属学院不存在");
        }
    }
}
