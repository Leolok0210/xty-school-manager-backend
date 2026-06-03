package com.xiaotiyun.school.manager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiaotiyun.school.manager.model.entity.StudentUsuallyRuleEntity;
import com.xiaotiyun.school.manager.model.req.StudentUsuallyRuleDepartmentReqModel;
import com.xiaotiyun.school.manager.model.req.StudentUsuallyRuleReqModel;
import com.xiaotiyun.school.manager.model.res.StudentUsuallyRuleImportResModel;
import com.xiaotiyun.school.manager.model.res.StudentUsuallyRuleResModel;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

public interface StudentUsuallyRuleService extends IService<StudentUsuallyRuleEntity> {

    void updateRule(Long schoolId ,List<StudentUsuallyRuleDepartmentReqModel> reqModel);

    List<StudentUsuallyRuleResModel> getRule(Long schoolId);

    List<StudentUsuallyRuleResModel> listByGroupId(Long schoolId, @Valid StudentUsuallyRuleReqModel groupId);

    List<StudentUsuallyRuleImportResModel> listImportData(Long schoolId);

    Long importRules(MultipartFile file, Long schoolId);
}