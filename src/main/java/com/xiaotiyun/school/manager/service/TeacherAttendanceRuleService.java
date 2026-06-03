package com.xiaotiyun.school.manager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.model.entity.TeacherAttendanceRule;
import com.xiaotiyun.school.manager.model.req.TeacherAttendanceRulePageReqModel;
import com.xiaotiyun.school.manager.model.req.TeacherAttendanceRuleSaveReqModel;
import com.xiaotiyun.school.manager.model.res.TeacherAttendanceRulePageResModel;

import java.util.List;

public interface TeacherAttendanceRuleService extends IService<TeacherAttendanceRule> {
    List<Long> selectedUserIds(Long schoolId);

    PageInfo<TeacherAttendanceRulePageResModel> page(TeacherAttendanceRulePageReqModel reqModel);

    void save(TeacherAttendanceRuleSaveReqModel reqModel);

    void update(Long id, TeacherAttendanceRuleSaveReqModel reqModel);

    void delete(Long id);
}