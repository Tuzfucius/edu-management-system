package com.tzufucius.edu.edumanagementsystem.service;

import com.tzufucius.edu.edumanagementsystem.dao.ClassInfoDao;
import com.tzufucius.edu.edumanagementsystem.dao.MajorDao;
import com.tzufucius.edu.edumanagementsystem.entity.ClassInfo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClassInfoService {

    private final ClassInfoDao classInfoDao;
    private final MajorDao majorDao;

    public ClassInfoService(ClassInfoDao classInfoDao, MajorDao majorDao) {
        this.classInfoDao = classInfoDao;
        this.majorDao = majorDao;
    }

    /**
     * 查询全部正常班级。
     *
     * @return 正常状态的班级列表
     */
    public List<ClassInfo> findAll() {
        return classInfoDao.findAll();
    }

    /**
     * 根据班级ID查询班级详情。
     *
     * @param id 班级ID
     * @return 班级详情
     * @throws RuntimeException 班级ID为空或班级不存在时抛出
     */
    public ClassInfo findById(Long id) {
        if (id == null) {
            throw new RuntimeException("班级ID不能为空");
        }

        ClassInfo classInfo = classInfoDao.findById(id);
        if (classInfo == null) {
            throw new RuntimeException("班级不存在");
        }

        return classInfo;
    }

    /**
     * 新增班级，并校验必填字段、所属专业和班级编号唯一性。
     *
     * @param classInfo 待新增的班级信息
     * @throws RuntimeException 班级信息不合法、专业不存在、编号重复或新增失败时抛出
     */
    public void addClassInfo(ClassInfo classInfo) {
        checkClassInfoForAdd(classInfo);
        checkMajorExists(classInfo.getMajorId());

        int exists = classInfoDao.countByClassCode(classInfo.getClassCode());
        if (exists > 0) {
            throw new RuntimeException("班级编号已存在");
        }

        int rows = classInfoDao.insert(classInfo);
        if (rows != 1) {
            throw new RuntimeException("新增班级失败");
        }
    }

    /**
     * 修改班级，并校验目标班级、所属专业和班级编号唯一性。
     *
     * @param classInfo 待修改的班级信息
     * @throws RuntimeException 班级信息不合法、班级不存在、专业不存在、编号重复或修改失败时抛出
     */
    public void updateClassInfo(ClassInfo classInfo) {
        checkClassInfoForUpdate(classInfo);

        ClassInfo oldClassInfo = classInfoDao.findById(classInfo.getId());
        if (oldClassInfo == null) {
            throw new RuntimeException("班级不存在，无法修改");
        }

        checkMajorExists(classInfo.getMajorId());

        int exists = classInfoDao.countByClassCodeExcludeId(classInfo.getClassCode(), classInfo.getId());
        if (exists > 0) {
            throw new RuntimeException("班级编号已存在");
        }

        int rows = classInfoDao.update(classInfo);
        if (rows != 1) {
            throw new RuntimeException("修改班级失败");
        }
    }

    /**
     * 逻辑删除班级。删除前会校验班级是否存在，以及班级下是否仍有正常学生。
     *
     * @param id 班级ID
     * @throws RuntimeException 班级ID为空、班级不存在、仍有关联学生或删除失败时抛出
     */
    public void deleteClassInfo(Long id) {
        if (id == null) {
            throw new RuntimeException("班级ID不能为空");
        }

        ClassInfo classInfo = classInfoDao.findById(id);
        if (classInfo == null) {
            throw new RuntimeException("班级不存在，无法删除");
        }

        int studentCount = classInfoDao.countStudentByClassId(id);
        if (studentCount > 0) {
            throw new RuntimeException("该班级下仍有学生，不能删除");
        }

        int rows = classInfoDao.disableById(id);
        if (rows != 1) {
            throw new RuntimeException("删除班级失败");
        }
    }

    /**
     * 校验新增班级需要的基础字段。
     *
     * @param classInfo 待新增的班级信息
     * @throws RuntimeException 班级对象、所属专业、班级编号、班级名称或入学年份不合法时抛出
     */
    private void checkClassInfoForAdd(ClassInfo classInfo) {
        if (classInfo == null) {
            throw new RuntimeException("班级信息不能为空");
        }

        checkRequiredFields(classInfo);
    }

    /**
     * 校验修改班级需要的基础字段。
     *
     * @param classInfo 待修改的班级信息
     * @throws RuntimeException 班级对象、班级ID、所属专业、班级编号、班级名称或入学年份不合法时抛出
     */
    private void checkClassInfoForUpdate(ClassInfo classInfo) {
        if (classInfo == null) {
            throw new RuntimeException("班级信息不能为空");
        }

        if (classInfo.getId() == null) {
            throw new RuntimeException("班级ID不能为空");
        }

        checkRequiredFields(classInfo);
    }

    private void checkRequiredFields(ClassInfo classInfo) {
        if (classInfo.getMajorId() == null) {
            throw new RuntimeException("所属专业不能为空");
        }

        if (classInfo.getClassCode() == null || classInfo.getClassCode().isBlank()) {
            throw new RuntimeException("班级编号不能为空");
        }

        if (classInfo.getClassName() == null || classInfo.getClassName().isBlank()) {
            throw new RuntimeException("班级名称不能为空");
        }

        if (classInfo.getEntranceYear() == null || classInfo.getEntranceYear() <= 0) {
            throw new RuntimeException("入学年份必须大于0");
        }
    }

    private void checkMajorExists(Long majorId) {
        if (majorDao.findById(majorId) == null) {
            throw new RuntimeException("所属专业不存在");
        }
    }
}
