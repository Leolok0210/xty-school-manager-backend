package com.xiaotiyun.school.manager.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import com.xiaotiyun.school.manager.basic.exception.BusinessException;
import com.xiaotiyun.school.manager.dao.SubjectLevelRuleDao;
import com.xiaotiyun.school.manager.model.entity.Subject;
import com.xiaotiyun.school.manager.model.entity.SubjectLevelRuleEntity;
import com.xiaotiyun.school.manager.model.entity.SubjectRelEntity;
import com.xiaotiyun.school.manager.model.req.SubjectLevelRuleDetailReqModel;
import com.xiaotiyun.school.manager.model.req.SubjectLevelRuleReqModel;
import com.xiaotiyun.school.manager.model.req.SubjectQueryReqModel;
import com.xiaotiyun.school.manager.model.req.SubjectRelGroupQueryReqModel;
import com.xiaotiyun.school.manager.model.res.SubjectDetailResModel;
import com.xiaotiyun.school.manager.model.res.SubjectLevelRuleDetailResModel;
import com.xiaotiyun.school.manager.model.res.SubjectLevelRuleResModel;
import com.xiaotiyun.school.manager.model.res.SubjectRelResModel;
import com.xiaotiyun.school.manager.service.SubjectLevelRuleService;
import com.xiaotiyun.school.manager.service.SubjectRelService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SubjectLevelRuleServiceImpl extends ServiceImpl<SubjectLevelRuleDao, SubjectLevelRuleEntity> implements SubjectLevelRuleService {

    @Resource
    private SubjectRelService subjectRelService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRule(Long schoolId, SubjectLevelRuleReqModel reqModel) {
        // 获取科目信息
        SubjectRelEntity subjectRel = subjectRelService.getById(reqModel.getSubjectId());
        if (subjectRel == null) {
            throw new BusinessException(LanguageConstants.SUBJECT_ID_NOT_EXISTS);
        }
        List<SubjectLevelRuleDetailReqModel> detailList = reqModel.getDetailList();
        // 不论评级还是分数，都需要存下规则
        if (ObjectUtils.isEmpty(detailList)) {
            throw new BusinessException(LanguageConstants.SUBJECT_LEVEL_RULE_NOT_EXISTS);
        }
        // 清空原规则
        remove(Wrappers.<SubjectLevelRuleEntity>lambdaQuery()
                .eq(SubjectLevelRuleEntity::getSubjectId, reqModel.getSubjectId())
                .eq(SubjectLevelRuleEntity::getGroupId, reqModel.getGroupId())
                .eq(SubjectLevelRuleEntity::getSchoolId, schoolId));
        // 塞入新规则
        List<SubjectLevelRuleEntity> insertList = new ArrayList<>();
        for (SubjectLevelRuleDetailReqModel rule : detailList) {
            SubjectLevelRuleEntity entity = new SubjectLevelRuleEntity();
            BeanUtils.copyProperties(rule, entity);
            entity.setGroupId(reqModel.getGroupId());
            entity.setSchoolId(schoolId);
            entity.setSubjectId(reqModel.getSubjectId());
            entity.setDeleted(0L);
            insertList.add(entity);
        }
        saveBatch(insertList);
        // 更新科目成绩展示规则
        if (!Objects.equals(reqModel.getShowRule(), subjectRel.getShowRule())) {
            subjectRel.setShowRule(reqModel.getShowRule());
            subjectRelService.updateById(subjectRel);
        }
    }

    @Override
    public List<SubjectLevelRuleResModel> getRuleByDepartment(Long schoolId,Long groupId) {
        SubjectRelGroupQueryReqModel queryWrapper = new SubjectRelGroupQueryReqModel();
        queryWrapper.setSchoolId(schoolId);
        queryWrapper.setGroupId(groupId);
        List<SubjectRelResModel> relResModels = subjectRelService.listByGroup(queryWrapper);
        if (ObjectUtils.isEmpty(relResModels))
        {
            return new ArrayList<>();
        }
        // 获取科目
        List<Long> subjectIds = relResModels.stream().map(SubjectRelResModel::getId).collect(Collectors.toList());
        // 获取科目评级规则
        List<SubjectLevelRuleResModel> result = new ArrayList<>();
        Map<Long, List<SubjectLevelRuleEntity>> subjectIdMap = new HashMap<>();
        if (ObjectUtils.isNotEmpty(subjectIds)) {
            List<SubjectLevelRuleEntity> rules = list(Wrappers.<SubjectLevelRuleEntity>lambdaQuery()
                    .eq(SubjectLevelRuleEntity::getGroupId, groupId)
                    .eq(SubjectLevelRuleEntity::getSchoolId, schoolId)
                    .in(SubjectLevelRuleEntity::getSubjectId, subjectIds));
            if (ObjectUtils.isNotEmpty(rules)) {
                subjectIdMap = rules.stream().collect(Collectors.groupingBy(SubjectLevelRuleEntity::getSubjectId));
            }
        }
        // 拼接返回数据
        for (SubjectRelResModel subjectDetailResModel : relResModels) {
            SubjectLevelRuleResModel resModel = new SubjectLevelRuleResModel();
            resModel.setShowRule(subjectDetailResModel.getShowRule());
            SubjectDetailResModel subject = subjectDetailResModel.getSubject();
            if (subject != null) {
                resModel.setSubjectName(subject.getSubjectName());
                resModel.setSubjectNumber(subject.getSubjectNumber());
            }
            resModel.setSubjectId(subjectDetailResModel.getId());
            List<SubjectLevelRuleEntity> subjectLevelRuleEntities = subjectIdMap.get(subjectDetailResModel.getId());
            if (ObjectUtils.isNotEmpty(subjectLevelRuleEntities)) {
                List<SubjectLevelRuleDetailResModel> detailList = new ArrayList<>();
                subjectLevelRuleEntities.forEach(rule -> {
                    SubjectLevelRuleDetailResModel detailResModel = new SubjectLevelRuleDetailResModel();
                    BeanUtils.copyProperties(rule, detailResModel);
                    detailList.add(detailResModel);
                });
                resModel.setDetailList(detailList);
            }
            result.add(resModel);
        }
        return result;
    }
}