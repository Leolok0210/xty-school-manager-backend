package com.xiaotiyun.school.manager.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaotiyun.school.manager.basic.exception.BusinessException;
import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import com.xiaotiyun.school.manager.basic.exception.BusinessMessageException;
import com.xiaotiyun.school.manager.basic.util.LanguageUtil;
import com.xiaotiyun.school.manager.dao.QualityEvaluationCommentRuleDao;
import com.xiaotiyun.school.manager.model.dto.ConditionGroupDTO;
import com.xiaotiyun.school.manager.model.dto.QualityEvaluationCommentRuleDTO;
import com.xiaotiyun.school.manager.model.entity.QualityEvaluationCommentRuleEntity;
import com.xiaotiyun.school.manager.model.enums.CommentTemplateVarEnum;
import com.xiaotiyun.school.manager.model.enums.ConditionItemEnum;
import com.xiaotiyun.school.manager.model.enums.ConditionCombineTypeEnum;
import com.xiaotiyun.school.manager.model.enums.OperatorEnum;
import com.xiaotiyun.school.manager.model.req.QualityCommentRuleBatchOperateReqModel;
import com.xiaotiyun.school.manager.service.QualityCommentRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QualityCommentRuleServiceImpl extends ServiceImpl<QualityEvaluationCommentRuleDao, QualityEvaluationCommentRuleEntity> implements QualityCommentRuleService {

    @Autowired
    private LanguageUtil languageUtil;
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchOperateRules(Long schoolId, QualityCommentRuleBatchOperateReqModel reqModel) {
        // 处理删除
        if (reqModel.getDeleteIds() != null && !reqModel.getDeleteIds().isEmpty()) {
            // 校验权限
            List<QualityEvaluationCommentRuleEntity> deleteRules = this.listByIds(reqModel.getDeleteIds());
            deleteRules.forEach(rule -> {
                if (!rule.getSchoolId().equals(schoolId)) {
                    throw new BusinessException(LanguageConstants.NO_PERMISSION_DELETE_OTHER_SCHOOL_RULE);
                }
            });
            // 执行删除
            this.removeByIds(reqModel.getDeleteIds());
        }
        
        // 处理新增和编辑
        if (reqModel.getSaveRules() != null && !reqModel.getSaveRules().isEmpty()) {
            reqModel.getSaveRules().forEach(rule -> {
                // 校验条件组合类型和条件项
                rule.getConditions().forEach(group -> {
                    // 校验条件组合类型
                    if (!Arrays.stream(ConditionCombineTypeEnum.values())
                            .anyMatch(type -> type.getCode().equals(group.getCombineType()))) {
                        throw new BusinessException(LanguageConstants.INVALID_CONDITION_COMBINE_TYPE);
                    }
                    
                    // 校验条件项
                    group.getItems().forEach(item -> {
                        // 校验条件项目
                        if (!Arrays.stream(ConditionItemEnum.values())
                                .anyMatch(condItem -> condItem.name().equals(item.getItem()))) {
                            throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.INVALID_CONDITION_ITEM) + ": " + item.getItem());
                        }
                        
                        // 校验运算符
                        if (!Arrays.stream(OperatorEnum.values())
                                .anyMatch(op -> op.getSymbol().equals(item.getOperator()))) {
                            throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.INVALID_OPERATOR) + ": " + item.getOperator());
                        }
                    });
                });
                
                // 校验评语模板变量
                String template = rule.getCommentTemplate();
                int start = 0;
                while ((start = template.indexOf("{", start)) >= 0) {
                    int end = template.indexOf("}", start);
                    if (end < 0) {
                        throw new BusinessException(LanguageConstants.COMMENT_TEMPLATE_VAR_FORMAT_ERROR);
                    }
                    String var = template.substring(start + 1, end);
                    if (CommentTemplateVarEnum.getByCode(var) == null) {
                        throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.INVALID_COMMENT_TEMPLATE_VAR) + ": " + var);
                    }
                    start = end + 1;
                }
                
                QualityEvaluationCommentRuleEntity entity;
                if (rule.getId() != null) {
                    // 编辑
                    entity = this.getById(rule.getId());
                    if (entity == null) {
                        throw new BusinessException(LanguageConstants.COMMENT_RULE_NOT_EXISTS);
                    }
                    if (!entity.getSchoolId().equals(schoolId)) {
                        throw new BusinessException(LanguageConstants.NO_PERMISSION_EDIT_OTHER_SCHOOL_RULE);
                    }
                } else {
                    // 新增
                    entity = new QualityEvaluationCommentRuleEntity();
                    entity.setSchoolId(schoolId);
                }
                
                entity.setRuleName(rule.getRuleName());
                entity.setPriority(rule.getPriority());
                entity.setConditions(JSON.toJSONString(rule.getConditions()));
                entity.setCommentTemplate(rule.getCommentTemplate());
                entity.setStatus(rule.getStatus());
                
                this.saveOrUpdate(entity);
            });
        }
    }
    
    @Override
    public List<QualityEvaluationCommentRuleEntity> listRules(Long schoolId) {
        return this.list(new LambdaQueryWrapper<QualityEvaluationCommentRuleEntity>()
                .eq(QualityEvaluationCommentRuleEntity::getSchoolId, schoolId)
                .eq(QualityEvaluationCommentRuleEntity::getDeleted, 0)
                .orderByAsc(QualityEvaluationCommentRuleEntity::getPriority));
    }

    @Override
    public List<QualityEvaluationCommentRuleDTO> listRulesDTO(Long schoolId) {
        List<QualityEvaluationCommentRuleEntity> rules = this.listRules(schoolId);
        if (rules != null) {
            return rules.stream().map(rule -> {
                QualityEvaluationCommentRuleDTO dto = new QualityEvaluationCommentRuleDTO();
                dto.setRuleName(rule.getRuleName());
                dto.setPriority(rule.getPriority());
                dto.setConditions(JSON.parseArray(rule.getConditions(), ConditionGroupDTO.class));
                dto.setCommentTemplate(rule.getCommentTemplate());
                dto.setSchoolId(rule.getSchoolId());
                return dto;
            }).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }
} 