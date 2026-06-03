package com.xiaotiyun.school.manager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiaotiyun.school.manager.model.entity.SysSemesterRuleEntity;
import com.xiaotiyun.school.manager.model.req.SysSemesterRuleAddReqModel;
import com.xiaotiyun.school.manager.model.res.SysSemesterRuleAddDetailResModel;
import com.xiaotiyun.school.manager.model.res.SysSemesterRuleResModel;

import java.util.List;

public interface SysSemesterRuleService extends IService<SysSemesterRuleEntity> {
    void updateRule(SysSemesterRuleAddReqModel reqModel);

    //根据学年和学部获取数据
    List<SysSemesterRuleAddDetailResModel> getRuleBySchoolYearAndDepartment(String schoolYear, Long groupId,Long schoolId);
    List<SysSemesterRuleResModel> getRuleById(Long schoolId, String schoolYear);
}