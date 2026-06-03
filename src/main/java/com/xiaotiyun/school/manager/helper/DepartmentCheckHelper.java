package com.xiaotiyun.school.manager.helper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xiaotiyun.school.manager.basic.util.LanguageUtil;
import com.xiaotiyun.school.manager.dao.*;
import com.xiaotiyun.school.manager.model.entity.*;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 学部校验辅助类
 */
@Component
public class DepartmentCheckHelper {

    @Resource
    private SemesterDao semesterDao;

    @Resource
    private GradeGroupMapper gradeGroupDao;

    @Resource
    private SysClassDao sysClassDao;

    @Resource
    private SubjectDao subjectDao;

    /**
     * 校验学部是否可以取消勾选
     * 如果学部下已经有数据，则不允许取消勾选
     *
     * @param departmentId 学部ID
     * @param schoolId     学校ID
     * @return true: 可以取消勾选, false: 不可以取消勾选
     */
    public boolean canUnselectDepartment(Integer departmentId, Long schoolId) {
        // 1. 检查是否有学段数据
        if (semesterDao.selectCount(new LambdaQueryWrapper<SemesterEntity>()
                .eq(SemesterEntity::getSchoolId, schoolId)
                .eq(SemesterEntity::getDepartment, departmentId)
                .eq(SemesterEntity::getDeleted, 0)) > 0) {
            return false;
        }

        // 2. 检查是否有级组数据
        if (gradeGroupDao.selectCount(new LambdaQueryWrapper<GradeGroup>()
                .eq(GradeGroup::getSchoolId, schoolId)
                .eq(GradeGroup::getDepartment, departmentId)
                .eq(GradeGroup::getDeleted, 0)) > 0) {
            return false;
        }

        // 3. 检查是否有班级数据
        if (sysClassDao.selectCount(new LambdaQueryWrapper<SysClass>()
                .eq(SysClass::getSchoolId, schoolId)
                .eq(SysClass::getDepartment, departmentId)
                .eq(SysClass::getDeleted, 0)) > 0) {
            return false;
        }

        // 4. 检查是否有科目数据
//        if (subjectDao.selectCount(new LambdaQueryWrapper<Subject>()
//                .eq(Subject::getSchoolId, schoolId)
//                .eq(Subject::getDepartment, departmentId)
//                .eq(Subject::getDeleted, 0)) > 0) {
//            return false;
//        }

        return true;
    }
}