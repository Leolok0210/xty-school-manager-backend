package com.xiaotiyun.school.manager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiaotiyun.school.manager.model.dto.QualityEvaluationCommentRuleDTO;
import com.xiaotiyun.school.manager.model.entity.QualityEvaluationCommentRuleEntity;
import com.xiaotiyun.school.manager.model.req.QualityCommentRuleBatchOperateReqModel;
import java.util.List;

public interface QualityCommentRuleService extends IService<QualityEvaluationCommentRuleEntity> {
    
    void batchOperateRules(Long schoolId, QualityCommentRuleBatchOperateReqModel reqModel);
    
    List<QualityEvaluationCommentRuleEntity> listRules(Long schoolId);

    List<QualityEvaluationCommentRuleDTO> listRulesDTO(Long schoolId);
} 