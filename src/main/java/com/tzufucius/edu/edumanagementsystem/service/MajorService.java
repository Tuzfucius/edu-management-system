package com.tzufucius.edu.edumanagementsystem.service;

import com.tzufucius.edu.edumanagementsystem.dao.CollegeDao;
import com.tzufucius.edu.edumanagementsystem.dao.MajorDao;
import com.tzufucius.edu.edumanagementsystem.entity.Major;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MajorService {

    private final MajorDao majorDao;
    private final CollegeDao collegeDao;

    public MajorService(MajorDao majorDao, CollegeDao collegeDao) {
        this.majorDao = majorDao;
        this.collegeDao = collegeDao;
    }

    /**
     * 查询全部正常专业。
     *
     * @return 正常状态的专业列表
     */
    public List<Major> findAll() {
        return majorDao.findAll();
    }

    /**
     * 根据专业ID查询专业详情。
     *
     * @param id 专业ID
     * @return 专业详情
     * @throws RuntimeException 专业ID为空或专业不存在时抛出
     */
    public Major findById(Long id) {
        if (id == null) {
            throw new RuntimeException("专业ID不能为空");
        }

        Major major = majorDao.findById(id);
        if (major == null) {
            throw new RuntimeException("专业不存在");
        }

        return major;
    }

    /**
     * 新增专业，并校验必填字段、所属学院和专业编号唯一性。
     *
     * @param major 待新增的专业信息
     * @throws RuntimeException 专业信息不合法、学院不存在、编号重复或新增失败时抛出
     */
    public void addMajor(Major major) {
        checkMajorForAdd(major);
        checkCollegeExists(major.getCollegeId());

        int exists = majorDao.countByMajorCode(major.getMajorCode());
        if (exists > 0) {
            throw new RuntimeException("专业编号已存在");
        }

        int rows = majorDao.insert(major);
        if (rows != 1) {
            throw new RuntimeException("新增专业失败");
        }
    }

    /**
     * 修改专业，并校验目标专业、所属学院和专业编号唯一性。
     *
     * @param major 待修改的专业信息
     * @throws RuntimeException 专业信息不合法、专业不存在、学院不存在、编号重复或修改失败时抛出
     */
    public void updateMajor(Major major) {
        checkMajorForUpdate(major);

        Major oldMajor = majorDao.findById(major.getId());
        if (oldMajor == null) {
            throw new RuntimeException("专业不存在，无法修改");
        }

        checkCollegeExists(major.getCollegeId());

        int exists = majorDao.countByMajorCodeExcludeId(major.getMajorCode(), major.getId());
        if (exists > 0) {
            throw new RuntimeException("专业编号已存在");
        }

        int rows = majorDao.update(major);
        if (rows != 1) {
            throw new RuntimeException("修改专业失败");
        }
    }

    /**
     * 逻辑删除专业。删除前会校验专业是否存在，以及专业下是否仍有正常班级。
     *
     * @param id 专业ID
     * @throws RuntimeException 专业ID为空、专业不存在、仍有关联班级或删除失败时抛出
     */
    public void deleteMajor(Long id) {
        if (id == null) {
            throw new RuntimeException("专业ID不能为空");
        }

        Major major = majorDao.findById(id);
        if (major == null) {
            throw new RuntimeException("专业不存在，无法删除");
        }

        int classCount = majorDao.countClassByMajorId(id);
        if (classCount > 0) {
            throw new RuntimeException("该专业下仍有班级，不能删除");
        }

        int rows = majorDao.disableById(id);
        if (rows != 1) {
            throw new RuntimeException("删除专业失败");
        }
    }

    /**
     * 校验新增专业需要的基础字段。
     *
     * @param major 待新增的专业信息
     * @throws RuntimeException 专业对象、所属学院、专业编号、专业名称或学制不合法时抛出
     */
    private void checkMajorForAdd(Major major) {
        if (major == null) {
            throw new RuntimeException("专业信息不能为空");
        }

        checkRequiredFields(major);
    }

    /**
     * 校验修改专业需要的基础字段。
     *
     * @param major 待修改的专业信息
     * @throws RuntimeException 专业对象、专业ID、所属学院、专业编号、专业名称或学制不合法时抛出
     */
    private void checkMajorForUpdate(Major major) {
        if (major == null) {
            throw new RuntimeException("专业信息不能为空");
        }

        if (major.getId() == null) {
            throw new RuntimeException("专业ID不能为空");
        }

        checkRequiredFields(major);
    }

    private void checkRequiredFields(Major major) {
        if (major.getCollegeId() == null) {
            throw new RuntimeException("所属学院不能为空");
        }

        if (major.getMajorCode() == null || major.getMajorCode().isBlank()) {
            throw new RuntimeException("专业编号不能为空");
        }

        if (major.getMajorName() == null || major.getMajorName().isBlank()) {
            throw new RuntimeException("专业名称不能为空");
        }

        if (major.getSchoolingYears() == null || major.getSchoolingYears() <= 0) {
            throw new RuntimeException("学制必须大于0");
        }
    }

    private void checkCollegeExists(Long collegeId) {
        if (collegeDao.findById(collegeId) == null) {
            throw new RuntimeException("所属学院不存在");
        }
    }
}
