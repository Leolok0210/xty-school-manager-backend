package com.xiaotiyun.school.manager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiaotiyun.school.manager.model.entity.SubjectLevelRuleEntity;
import com.xiaotiyun.school.manager.model.req.SubjectLevelRuleReqModel;
import com.xiaotiyun.school.manager.model.res.SubjectLevelRuleResModel;

import java.util.List;

public interface SubjectLevelRuleService extends IService<SubjectLevelRuleEntity> {

    void updateRule(Long schoolId, SubjectLevelRuleReqModel reqModel);

    List<SubjectLevelRuleResModel> getRuleByDepartment(Long schoolId, Long groupId);


}