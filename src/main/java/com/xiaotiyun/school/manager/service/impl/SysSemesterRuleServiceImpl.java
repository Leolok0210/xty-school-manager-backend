package com.xiaotiyun.school.manager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import com.xiaotiyun.school.manager.basic.exception.BusinessException;
import com.xiaotiyun.school.manager.dao.SysSemesterRuleDao;
import com.xiaotiyun.school.manager.model.entity.GradeGroup;
import com.xiaotiyun.school.manager.model.entity.SemesterEntity;
import com.xiaotiyun.school.manager.model.entity.SysSemesterRuleEntity;
import com.xiaotiyun.school.manager.model.req.GradeGroupQueryReqModel;
import com.xiaotiyun.school.manager.model.req.SysSemesterRuleAddReqModel;
import com.xiaotiyun.school.manager.model.res.SysSemesterRuleAddDetailResModel;
import com.xiaotiyun.school.manager.model.res.SysSemesterRuleResModel;
import com.xiaotiyun.school.manager.service.GradeGroupService;
import com.xiaotiyun.school.manager.service.SemesterService;
import com.xiaotiyun.school.manager.service.SysSemesterRuleService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class SysSemesterRuleServiceImpl extends ServiceImpl<SysSemesterRuleDao, SysSemesterRuleEntity> implements SysSemesterRuleService {

    @Resource
    private SemesterService semesterService;


    @Resource
    private GradeGroupService gradeGroupService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRule(SysSemesterRuleAddReqModel reqModel) {
        if (ObjectUtils.isEmpty(reqModel.getDetails())) {
            throw new BusinessException(LanguageConstants.PARAM_ERROR);
        }
        // 清空原规则
        remove(Wrappers.<SysSemesterRuleEntity>lambdaQuery()
                .eq(SysSemesterRuleEntity::getSchoolId, reqModel.getSchoolId())
                .eq(SysSemesterRuleEntity::getSchoolYear, reqModel.getSchoolYear()));
        // 塞入新规则
        List<SysSemesterRuleEntity> insertList = new ArrayList<>();
        reqModel.getDetails().forEach(rule -> {
            if (ObjectUtils.isEmpty(rule.getDetails())) {
                throw new BusinessException(LanguageConstants.PARAM_ERROR);
            }
            rule.getDetails().forEach(detail -> {
                SysSemesterRuleEntity entity = new SysSemesterRuleEntity();
                BeanUtils.copyProperties(detail, entity);
                entity.setSchoolId(reqModel.getSchoolId());
                entity.setSchoolYear(reqModel.getSchoolYear());
                entity.setGroupId(rule.getGroupId());
                entity.setDeleted(0L);
                insertList.add(entity);
            });
        });
        saveBatch(insertList);
    }

    @Override
    public List<SysSemesterRuleResModel> getRuleById(Long schoolId, String schoolYear) {
        List<SysSemesterRuleResModel> resList = new ArrayList<>();
        //级组信息
        GradeGroupQueryReqModel gradeGroupQueryReqModel = new GradeGroupQueryReqModel();
        gradeGroupQueryReqModel.setSchoolId(schoolId);
        gradeGroupQueryReqModel.setSid(schoolYear);
        List<GradeGroup> gradeAllGroupList = gradeGroupService.getGradeAllGroupList(gradeGroupQueryReqModel);
        if (CollectionUtils.isEmpty(gradeAllGroupList))
        {
            return resList;
        }
        //to maplsit
        Map<Long, GradeGroup> groupMap = gradeAllGroupList.stream().collect(Collectors.toMap(GradeGroup::getId, item -> item));
        // 获取学段信息
        List<SemesterEntity> semesterList = semesterService.list(Wrappers.<SemesterEntity>lambdaQuery()
                .eq(SemesterEntity::getSchoolId, schoolId)
                .eq(SemesterEntity::getSchoolYear, schoolYear));
        if (ObjectUtils.isNotEmpty(semesterList)) {
            Map<Integer, List<SemesterEntity>> departmentMap = semesterList.stream().collect(Collectors.groupingBy(SemesterEntity::getDepartment));
            // 获取权重规则
            List<SysSemesterRuleEntity> ruleList = list(Wrappers.<SysSemesterRuleEntity>lambdaQuery()
                    .eq(SysSemesterRuleEntity::getSchoolId, schoolId)
                    .eq(SysSemesterRuleEntity::getSchoolYear, schoolYear));
            Map<Long, List<SysSemesterRuleEntity>>  groupSemesterMap;
            if(!CollectionUtils.isEmpty(ruleList))
            {
                groupSemesterMap = ruleList.stream().collect(Collectors.groupingBy(SysSemesterRuleEntity::getGroupId));
            } else {
                groupSemesterMap = new HashMap<>();
            }
            // 组装返回参数
            groupMap.forEach((groupId, group) -> {
                SysSemesterRuleResModel resModel = new SysSemesterRuleResModel();
                resModel.setGroupId(group.getId());
                List<SysSemesterRuleAddDetailResModel> details = new ArrayList<>();
                List<SemesterEntity> semesterEntities = departmentMap.get(group.getDepartment().intValue());
                List<SysSemesterRuleEntity> sysSemesterRuleEntities = groupSemesterMap.get(groupId);
                Map<Long, SysSemesterRuleEntity> finalSemesterIdMap = new HashMap<>();
                //一个级组里面的学段不重复
                if(!CollectionUtils.isEmpty(sysSemesterRuleEntities))
                {
                    finalSemesterIdMap = sysSemesterRuleEntities.stream()
                            .collect(Collectors.toMap(SysSemesterRuleEntity::getSemesterId, Function.identity(),(x1,x2) -> x1));
                }
                if(!CollectionUtils.isEmpty(semesterEntities))
                {
                    for (SemesterEntity semesterEntity : semesterEntities) {
                        SysSemesterRuleAddDetailResModel detailResModel = new SysSemesterRuleAddDetailResModel();
                        detailResModel.setSemesterId(semesterEntity.getId());
                        detailResModel.setSemesterName(semesterEntity.getName());
                        // 插入权重信息
                        if (finalSemesterIdMap.containsKey(semesterEntity.getId())){
                            SysSemesterRuleEntity rule = finalSemesterIdMap.get(semesterEntity.getId());
                            detailResModel.setWeight(rule.getWeight());
                        }
                        details.add(detailResModel);
                    }
                }
                resModel.setDetails(details);
                resList.add(resModel);
            });
        }
        return resList;
    }

    @Override
    public List<SysSemesterRuleAddDetailResModel> getRuleBySchoolYearAndDepartment(String schoolYear, Long groupId,Long schoolId) {
        LambdaQueryWrapper<SysSemesterRuleEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysSemesterRuleEntity::getSchoolYear, schoolYear)
                .eq(SysSemesterRuleEntity::getSchoolId, schoolId)
                .eq(SysSemesterRuleEntity::getGroupId, groupId);
        List<SysSemesterRuleEntity> entity = list(queryWrapper);
        if (CollectionUtils.isEmpty(entity)) {
            return Collections.emptyList();
        }
        List<SysSemesterRuleAddDetailResModel> resModels = new ArrayList<>();
        for (SysSemesterRuleEntity sysSemesterRuleEntity : entity){
            SysSemesterRuleAddDetailResModel resModel = new SysSemesterRuleAddDetailResModel();
            resModel.setSemesterId(sysSemesterRuleEntity.getSemesterId());
            resModel.setWeight(sysSemesterRuleEntity.getWeight());
            resModels.add(resModel);
        }
        return resModels;
    }

}