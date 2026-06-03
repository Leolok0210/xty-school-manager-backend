package com.xiaotiyun.school.manager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiaotiyun.school.manager.model.entity.DepartmentScoreRuleEntity; // 修改实体类
import com.xiaotiyun.school.manager.model.req.DepartmentScoreRuleReqModel; // 修改请求类
import com.xiaotiyun.school.manager.model.res.DepartmentScoreRuleResModel; // 修改响应类

public interface DepartmentScoreRuleService extends IService<DepartmentScoreRuleEntity> {
    void updateRule(DepartmentScoreRuleReqModel reqModel);

    DepartmentScoreRuleResModel getRuleByDepartment(Long schoolId, Long groupId);


    Integer getAvgType(Long schoolId, Long groupId);
}