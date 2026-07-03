package com.tzufucius.edu.edumanagementsystem.service;

import com.tzufucius.edu.edumanagementsystem.dao.CollegeDao;
import com.tzufucius.edu.edumanagementsystem.entity.College;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CollegeService {

    private final CollegeDao collegeDao;

    public CollegeService(CollegeDao collegeDao) {
        this.collegeDao = collegeDao;
    }

    /**
     * 查询全部正常学院。
     *
     * @return 正常状态的学院列表
     */
    public List<College> findAll() {
        return collegeDao.findAll();
    }

    /**
     * 根据学院ID查询学院详情。
     *
     * @param id 学院ID
     * @return 学院详情
     * @throws RuntimeException 学院ID为空或学院不存在时抛出
     */
    public College findById(Long id) {
        if (id == null) {
            throw new RuntimeException("学院ID不能为空");
        }

        College college = collegeDao.findById(id);

        if (college == null) {
            throw new RuntimeException("学院不存在");
        }

        return college;
    }

    /**
     * 新增学院，并校验必填字段和学院编号唯一性。
     *
     * @param college 待新增的学院信息
     * @throws RuntimeException 学院信息不合法、编号重复或新增失败时抛出
     */
    public void addCollege(College college) {
        checkCollegeForAdd(college);

        int exists = collegeDao.countByCollegeCode(college.getCollegeCode());
        if (exists > 0) {
            throw new RuntimeException("学院编号已存在");
        }

        int rows = collegeDao.insert(college);
        if (rows != 1) {
            throw new RuntimeException("新增学院失败");
        }
    }

    /**
     * 修改学院，并校验目标学院是否存在以及学院编号是否与其他学院重复。
     *
     * @param college 待修改的学院信息
     * @throws RuntimeException 学院信息不合法、学院不存在、编号重复或修改失败时抛出
     */
    public void updateCollege(College college) {
        checkCollegeForUpdate(college);

        College oldCollege = collegeDao.findById(college.getId());
        if (oldCollege == null) {
            throw new RuntimeException("学院不存在，无法修改");
        }

        int exists = collegeDao.countByCollegeCodeExcludeId(college.getCollegeCode(), college.getId());
        if (exists > 0) {
            throw new RuntimeException("学院编号已存在");
        }

        int rows = collegeDao.update(college);
        if (rows != 1) {
            throw new RuntimeException("修改学院失败");
        }
    }

    /**
     * 逻辑删除学院。删除前会校验学院是否存在，以及学院下是否仍有正常专业。
     *
     * @param id 学院ID
     * @throws RuntimeException 学院ID为空、学院不存在、仍有关联专业或删除失败时抛出
     */
    public void deleteCollege(Long id) {
        if (id == null) {
            throw new RuntimeException("学院ID不能为空");
        }

        College college = collegeDao.findById(id);
        if (college == null) {
            throw new RuntimeException("学院不存在，无法删除");
        }

        int majorCount = collegeDao.countMajorByCollegeId(id);
        if (majorCount > 0) {
            throw new RuntimeException("该学院下仍有专业，不能删除");
        }

        int rows = collegeDao.disableById(id);
        if (rows != 1) {
            throw new RuntimeException("删除学院失败");
        }
    }

    /**
     * 校验新增学院需要的基础字段。
     *
     * @param college 待新增的学院信息
     * @throws RuntimeException 学院对象、学院编号或学院名称为空时抛出
     */
    private void checkCollegeForAdd(College college) {
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

    /**
     * 校验修改学院需要的基础字段。
     *
     * @param college 待修改的学院信息
     * @throws RuntimeException 学院对象、学院ID、学院编号或学院名称为空时抛出
     */
    private void checkCollegeForUpdate(College college) {
        if (college == null) {
            throw new RuntimeException("学院信息不能为空");
        }

        if (college.getId() == null) {
            throw new RuntimeException("学院ID不能为空");
        }

        if (college.getCollegeCode() == null || college.getCollegeCode().isBlank()) {
            throw new RuntimeException("学院编号不能为空");
        }

        if (college.getCollegeName() == null || college.getCollegeName().isBlank()) {
            throw new RuntimeException("学院名称不能为空");
        }
    }
}
