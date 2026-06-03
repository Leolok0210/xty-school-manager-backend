package com.xiaotiyun.school.manager.service.impl;

import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import com.xiaotiyun.school.manager.basic.enums.DepartmentScoreRuleEnum;
import com.xiaotiyun.school.manager.basic.exception.BusinessException;
import com.xiaotiyun.school.manager.dao.DepartmentScoreRuleDao;
import com.xiaotiyun.school.manager.model.entity.DepartmentScoreRuleEntity;
import com.xiaotiyun.school.manager.model.entity.GradeGroup;
import com.xiaotiyun.school.manager.model.entity.SystemSettingEntity;
import com.xiaotiyun.school.manager.model.req.DepartmentScoreRuleReqModel;
import com.xiaotiyun.school.manager.model.req.SubjectRelGroupQueryReqModel;
import com.xiaotiyun.school.manager.model.res.DepartmentScoreRuleDepartmentResModel;
import com.xiaotiyun.school.manager.model.res.DepartmentScoreRuleDetailResModel;
import com.xiaotiyun.school.manager.model.res.DepartmentScoreRuleResModel;
import com.xiaotiyun.school.manager.model.res.SubjectRelResModel;
import com.xiaotiyun.school.manager.service.DepartmentScoreRuleService;
import com.xiaotiyun.school.manager.service.GradeGroupService;
import com.xiaotiyun.school.manager.service.SubjectRelService;
import com.xiaotiyun.school.manager.service.SystemSettingService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class DepartmentScoreRuleServiceImpl extends ServiceImpl<DepartmentScoreRuleDao, DepartmentScoreRuleEntity> implements DepartmentScoreRuleService {


    @Resource
    private SubjectRelService subjectRelService;


    @Resource
    private GradeGroupService gradeGroupService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRule(DepartmentScoreRuleReqModel reqModel) {
        List<DepartmentScoreRuleEntity> insertList = new ArrayList<>();
        reqModel.getDetails().forEach(departmentReqModel -> {
            remove(Wrappers.<DepartmentScoreRuleEntity>lambdaQuery()
                    .eq(DepartmentScoreRuleEntity::getSchoolId, reqModel.getSchoolId())
                    .eq(DepartmentScoreRuleEntity::getGroupId, departmentReqModel.getGroupId()));
            //插入SCORE_AVG_RULE
            DepartmentScoreRuleEntity ruleEntity = new DepartmentScoreRuleEntity();
            ruleEntity.setSchoolId(reqModel.getSchoolId());
            ruleEntity.setGroupId(departmentReqModel.getGroupId());
            ruleEntity.setDeleted(0L);
            ruleEntity.setScoreRule(departmentReqModel.getAvgType());
            ruleEntity.setScoreType(DepartmentScoreRuleEnum.SCORE_AVG_RULE.getValue());
            insertList.add(ruleEntity);
            if (ObjectUtils.isNotEmpty(departmentReqModel.getDetails())) {
                departmentReqModel.getDetails().forEach(detail -> {
                    if (DepartmentScoreRuleEnum.isSubjectScore(detail.getScoreType()) &&
                            (detail.getSubjectId() == null || detail.getSubjectId() == 0L)) {
                        throw new BusinessException(LanguageConstants.PARAM_ERROR);
                    }
                    DepartmentScoreRuleEntity entity = new DepartmentScoreRuleEntity();
                    BeanUtils.copyProperties(detail, entity);
                    entity.setSchoolId(reqModel.getSchoolId());
                    entity.setGroupId(departmentReqModel.getGroupId());
                    entity.setDeleted(0L);
                    insertList.add(entity);
                });
            }else {
                if(departmentReqModel.getAvgType() == 1)
                    throw new BusinessException(LanguageConstants.PARAM_ERROR);
            }
        });
        saveBatch(insertList);
    }

    @Override
    public DepartmentScoreRuleResModel getRuleByDepartment(Long schoolId, Long groupId) {
        // 获取系统设置
        DepartmentScoreRuleResModel resModel = new DepartmentScoreRuleResModel();
        // 获取科目
        SubjectRelGroupQueryReqModel reqSub = new SubjectRelGroupQueryReqModel();
        reqSub.setSchoolId(schoolId);
        reqSub.setGroupId(groupId);
        List<SubjectRelResModel> relResModels = subjectRelService.listByGroup(reqSub);
        if (ObjectUtils.isEmpty(relResModels)) {
            return null;
        }
        List<DepartmentScoreRuleDepartmentResModel> departmentResModels = new ArrayList<>();
        if (ObjectUtils.isNotEmpty(relResModels)) {
            List<DepartmentScoreRuleEntity> ruleList = list(Wrappers.<DepartmentScoreRuleEntity>lambdaQuery()
                    .eq(DepartmentScoreRuleEntity::getGroupId, groupId)
                    .eq(DepartmentScoreRuleEntity::getSchoolId, schoolId));
            Map<String, Map<Long ,DepartmentScoreRuleEntity>> subjectIdRuleMap = new HashMap<>();
            if (ObjectUtils.isNotEmpty(ruleList)) {
                Map<String, List<DepartmentScoreRuleEntity>> typeListMap = ruleList.stream().collect(Collectors.groupingBy(DepartmentScoreRuleEntity::getScoreType));
                for (Map.Entry<String, List<DepartmentScoreRuleEntity>> entry : typeListMap.entrySet()){
                    subjectIdRuleMap.put(entry.getKey(), entry.getValue().stream().collect(Collectors.toMap(DepartmentScoreRuleEntity::getSubjectId, Function.identity())));
                }
            }
            // 组装返回参数
            DepartmentScoreRuleDepartmentResModel departmentResModel = new DepartmentScoreRuleDepartmentResModel();
            departmentResModel.setGroupId(groupId);
            //插入权重规则
            if(!CollectionUtils.isEmpty(ruleList))
            {
                List<DepartmentScoreRuleEntity> ruleEntities = ruleList.stream().filter(rule -> rule.getScoreType().equals(DepartmentScoreRuleEnum.SCORE_AVG_RULE.getValue())).collect(Collectors.toList());
                if(!CollectionUtils.isEmpty(ruleEntities))
                {
                    resModel.setAvgType(ruleEntities.get(0).getScoreRule());
                    //过滤掉tyee = 5的
                    ruleList = ruleList.stream().filter(rule -> !rule.getScoreType().equals(DepartmentScoreRuleEnum.SCORE_AVG_RULE.getValue()))
                            .collect(Collectors.toList());
                }
            }

            // 获取平时成绩和考试成绩权重
            List<DepartmentScoreRuleDetailResModel> details = ruleList.stream()
                    .filter(rule -> !DepartmentScoreRuleEnum.isSubjectScore(rule.getScoreType()))
                    .map(rule -> {
                        DepartmentScoreRuleDetailResModel detailResModel = new DepartmentScoreRuleDetailResModel();
                        BeanUtils.copyProperties(rule, detailResModel);
                        return detailResModel;
                    }).collect(Collectors.toList());
            if (ObjectUtils.isEmpty(details)) {
                DepartmentScoreRuleDetailResModel detailResModel = new DepartmentScoreRuleDetailResModel();
                detailResModel.setScoreType("0");
                details.add(detailResModel);
                DepartmentScoreRuleDetailResModel detailResModel1 = new DepartmentScoreRuleDetailResModel();
                detailResModel1.setScoreType("1");
                details.add(detailResModel1);
            }
            GradeGroup gradeGroup = gradeGroupService.getById(groupId);
            if(gradeGroup == null)
            {
                log.error("获取级组信息失败");
                return null;
            }
            // 获取科目成绩权重
            for (SubjectRelResModel subjectEntry : relResModels) {
                DepartmentScoreRuleDetailResModel detailResModel = new DepartmentScoreRuleDetailResModel();
                detailResModel.setSubjectName(subjectEntry.getSubject() == null ? null : subjectEntry.getSubject().getSubjectName());
                detailResModel.setSubjectEnglishName(subjectEntry.getSubject() == null ? null : subjectEntry.getSubject().getSubjectEnglishName());
                detailResModel.setSubjectId(subjectEntry.getId());
                if(gradeGroup.getProfessionalSubject() == 0)
                {
                    detailResModel.setScoreType(DepartmentScoreRuleEnum.SUBJECT_SCORE.getValue());
                }else {
                    if(subjectEntry.getArtsScience() == 1)
                    {
                        detailResModel.setScoreType(DepartmentScoreRuleEnum.COMMON_SCHOOL_SCORE.getValue());
                    }else if(subjectEntry.getArtsScience() == 2)
                    {
                        detailResModel.setScoreType(DepartmentScoreRuleEnum.COMMON_PROVINCE_SCORE.getValue());
                    }else if(subjectEntry.getArtsScience() == 3)
                    {
                        detailResModel.setScoreType(DepartmentScoreRuleEnum.COMMON_COMMERCE_SCORE.getValue());
                    } else {
                        //需要有三种  特殊处理
                        detailResModel.setScoreType(DepartmentScoreRuleEnum.COMMON_SCHOOL_SCORE.getValue());
                        DepartmentScoreRuleDetailResModel otherResModel = new DepartmentScoreRuleDetailResModel();
                        BeanUtils.copyProperties(detailResModel, otherResModel);
                        otherResModel.setScoreType(DepartmentScoreRuleEnum.COMMON_PROVINCE_SCORE.getValue());
                        if (subjectIdRuleMap.containsKey(otherResModel.getScoreType())) {
                            DepartmentScoreRuleEntity departmentScoreRuleEntity = subjectIdRuleMap.get(otherResModel.getScoreType()).get(detailResModel.getSubjectId());
                            if (departmentScoreRuleEntity != null && departmentScoreRuleEntity.getWeight() != null){
                                otherResModel.setWeight(departmentScoreRuleEntity.getWeight());
                            }
                        }
                        //COMMON_COMMERCE_SCORE
                        if (gradeGroup.getProfessionalSubject() == 3) {
                            DepartmentScoreRuleDetailResModel commerceResModel = new DepartmentScoreRuleDetailResModel();
                            BeanUtils.copyProperties(detailResModel, commerceResModel);
                            commerceResModel.setScoreType(DepartmentScoreRuleEnum.COMMON_COMMERCE_SCORE.getValue());
                            if (subjectIdRuleMap.containsKey(commerceResModel.getScoreType())) {
                                DepartmentScoreRuleEntity departmentScoreRuleEntity = subjectIdRuleMap.get(commerceResModel.getScoreType()).get(commerceResModel.getSubjectId());
                                if (departmentScoreRuleEntity != null && departmentScoreRuleEntity.getWeight() != null) {
                                    commerceResModel.setWeight(departmentScoreRuleEntity.getWeight());
                                }
                            }
                            details.add(commerceResModel);
                        }
                        details.add(otherResModel);
                    }
                }
                if (subjectIdRuleMap.containsKey(detailResModel.getScoreType())) {
                    DepartmentScoreRuleEntity departmentScoreRuleEntity = subjectIdRuleMap.get(detailResModel.getScoreType()).get(detailResModel.getSubjectId());
                    if (departmentScoreRuleEntity != null && departmentScoreRuleEntity.getWeight() != null){
                        detailResModel.setWeight(departmentScoreRuleEntity.getWeight());
                    }
                }
                details.add(detailResModel);
            }
            departmentResModel.setDetails(details);
            departmentResModels.add(departmentResModel);
        }
        resModel.setDetails(departmentResModels);
        return resModel;
    }

    @Override
    public Integer getAvgType(Long schoolId, Long groupId) {
        List<DepartmentScoreRuleEntity> ruleList = list(Wrappers.<DepartmentScoreRuleEntity>lambdaQuery()
                .eq(DepartmentScoreRuleEntity::getGroupId, groupId)
                .eq(DepartmentScoreRuleEntity::getScoreType, DepartmentScoreRuleEnum.SCORE_AVG_RULE.getValue())
                .eq(DepartmentScoreRuleEntity::getSchoolId, schoolId));
        if (ObjectUtils.isNotEmpty(ruleList)) {
            return ruleList.get(0).getScoreRule();
        }
        return 0;
    }


}