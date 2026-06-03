package com.xiaotiyun.school.manager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.model.entity.StudentAttendanceRuleEntity;
import com.xiaotiyun.school.manager.model.req.StudentAttendanceRulePageReqModel;
import com.xiaotiyun.school.manager.model.req.StudentAttendanceRuleSaveReqModel;
import com.xiaotiyun.school.manager.model.res.StudentAttendanceRulePageResModel;

import java.util.List;

public interface StudentAttendanceRuleService extends IService<StudentAttendanceRuleEntity> {

    List<Long> selectedGrades(Long schoolId);

    PageInfo<StudentAttendanceRulePageResModel> page(StudentAttendanceRulePageReqModel reqModel);

    void save(StudentAttendanceRuleSaveReqModel reqModel);

    void update(Long id, StudentAttendanceRuleSaveReqModel reqModel);

    void delete(Long id);
}