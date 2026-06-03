package com.xiaotiyun.school.manager.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.basic.exception.BusinessException;
import com.xiaotiyun.school.manager.basic.util.BeanConvertUtil;
import com.xiaotiyun.school.manager.basic.util.DateUtils;
import com.xiaotiyun.school.manager.dao.StudentAttendanceRuleDao;
import com.xiaotiyun.school.manager.model.entity.GradeGroup;
import com.xiaotiyun.school.manager.model.entity.StudentAttendanceRuleEntity;
import com.xiaotiyun.school.manager.model.req.StudentAttendanceRulePageReqModel;
import com.xiaotiyun.school.manager.model.req.StudentAttendanceRuleSaveReqModel;
import com.xiaotiyun.school.manager.model.res.StudentAttendanceRuleGradePageResModel;
import com.xiaotiyun.school.manager.model.res.StudentAttendanceRulePageResModel;
import com.xiaotiyun.school.manager.service.GradeGroupService;
import com.xiaotiyun.school.manager.service.StudentAttendanceRuleService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;

@Service
@RequiredArgsConstructor
public class StudentAttendanceRuleServiceImpl extends ServiceImpl<StudentAttendanceRuleDao, StudentAttendanceRuleEntity> implements StudentAttendanceRuleService {
    private final GradeGroupService gradeGroupService;

    @Override
    public List<Long> selectedGrades(Long schoolId) {
        QueryWrapper<StudentAttendanceRuleEntity> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(StudentAttendanceRuleEntity::getSchoolId, schoolId)
                .eq(StudentAttendanceRuleEntity::getDeleted, 0);
        List<StudentAttendanceRuleEntity> list = this.list(wrapper.lambda().orderByDesc(StudentAttendanceRuleEntity::getCreateTime));
        if (CollectionUtils.isNotEmpty(list)) {
            List<String> gradeIdStrs = list.stream().map(StudentAttendanceRuleEntity::getGrade).collect(Collectors.toList());
            List<Long> gradeIds = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(gradeIdStrs)) {
                gradeIdStrs.forEach(gradeIdStr -> {
                    gradeIds.addAll(JSONArray.parseArray(gradeIdStr).toJavaList(Long.class));
                });
            }
            return gradeIds;
        }
        return Collections.emptyList();
    }

    @Override
    public PageInfo<StudentAttendanceRulePageResModel> page(StudentAttendanceRulePageReqModel reqModel) {
        PageHelper.startPage(reqModel.getPageNum(), reqModel.getPageSize());
        QueryWrapper<StudentAttendanceRuleEntity> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(StudentAttendanceRuleEntity::getSchoolId, reqModel.getSchoolId())
                .eq(StudentAttendanceRuleEntity::getDeleted, 0);
        List<StudentAttendanceRuleEntity> list = this.list(wrapper.lambda().orderByDesc(StudentAttendanceRuleEntity::getCreateTime));
        if (CollectionUtils.isNotEmpty(list)) {
            //获取级组信息
            List<String> gradeIdStrs = list.stream().map(StudentAttendanceRuleEntity::getGrade).collect(Collectors.toList());
            List<Long> gradeIds = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(gradeIdStrs)) {
                gradeIdStrs.forEach(gradeIdStr -> {
                    gradeIds.addAll(JSONArray.parseArray(gradeIdStr).toJavaList(Long.class));
                });
            }
            Map<Long, GradeGroup> gradeGroupMap = new HashMap<>();
            if (CollectionUtils.isNotEmpty(gradeIds)) {
                List<GradeGroup> gradeGroups = gradeGroupService.listByIds(gradeIds);
                if (CollectionUtils.isNotEmpty(gradeGroups)) {
                    gradeGroupMap = gradeGroups.stream().collect(Collectors.toMap(GradeGroup::getId, gradeGroup -> gradeGroup));
                }
            }
            PageInfo<StudentAttendanceRuleEntity> pageInfo = new PageInfo<>(list);
            List<StudentAttendanceRulePageResModel> resList = new ArrayList<>();
            for (StudentAttendanceRuleEntity entity : list) {
                StudentAttendanceRulePageResModel resModel = new StudentAttendanceRulePageResModel();
                BeanUtils.copyProperties(entity, resModel);
                if (StringUtils.isNotBlank(entity.getGrade())) {
                    List<StudentAttendanceRuleGradePageResModel> grades = new ArrayList<>();
                    List<Long> gradeIdList = JSONArray.parseArray(entity.getGrade()).toJavaList(Long.class);
                    for (Long gradeId : gradeIdList) {
                        GradeGroup gradeGroup = gradeGroupMap.get(gradeId);
                        if (gradeGroup != null) {
                            StudentAttendanceRuleGradePageResModel grade = new StudentAttendanceRuleGradePageResModel();
                            grade.setGradeId(gradeGroup.getId());
                            grade.setGradeName(gradeGroup.getGradeGroupName());
                            grades.add(grade);
                        }
                    }
                    resModel.setGrades(grades);
                }
                resList.add(resModel);
            }
            PageInfo<StudentAttendanceRulePageResModel> result = new PageInfo<>(resList);
            result.setTotal(pageInfo.getTotal());
            result.setPages(pageInfo.getPages());
            return result;
        }
        return null;
    }

    @Override
    @Transactional
    public void save(StudentAttendanceRuleSaveReqModel reqModel) {
        if (!DateUtils.isTimeValid(reqModel.getMorningInTime(), reqModel.getAfternoonOutTime())) {
            throw new BusinessException(LanguageConstants.MORNING_IN_BEFORE_AFTERNOON_OUT);
        }
        List<Long> gradeIds = selectedGrades(reqModel.getSchoolId());
        if (CollectionUtils.isNotEmpty(gradeIds) && !Collections.disjoint(gradeIds, reqModel.getGradeIds())) {
            throw new BusinessException(LanguageConstants.GRADE_SINGLE_ATTENDANCE_RULE);
        }
        StudentAttendanceRuleEntity entity = BeanConvertUtil.convert(reqModel, StudentAttendanceRuleEntity.class);
        entity.setGrade(JSON.toJSONString(reqModel.getGradeIds()));
        this.save(entity);
    }

    @Override
    @Transactional
    public void update(Long id, StudentAttendanceRuleSaveReqModel reqModel) {
        if (!DateUtils.isTimeValid(reqModel.getMorningInTime(), reqModel.getAfternoonOutTime())) {
            throw new BusinessException(LanguageConstants.MORNING_IN_BEFORE_AFTERNOON_OUT);
        }
        StudentAttendanceRuleEntity entity = this.getById(id);
        if (entity == null) {
            throw new BusinessException(LanguageConstants.RECORD_NOT_EXIST);
        }
        List<Long> gradeIds = selectedGrades(reqModel.getSchoolId());
        if (CollectionUtils.isNotEmpty(gradeIds)) {
            List<Long> oldGradeIds = JSONArray.parseArray(entity.getGrade()).toJavaList(Long.class);
            List<Long> intersection = gradeIds.stream().filter(e -> !oldGradeIds.contains(e)).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(intersection) && !Collections.disjoint(intersection, reqModel.getGradeIds())) {
                throw new BusinessException(LanguageConstants.GRADE_SINGLE_ATTENDANCE_RULE);
            }
        }
        BeanUtils.copyProperties(reqModel, entity);
        entity.setGrade(JSON.toJSONString(reqModel.getGradeIds()));
        this.updateById(entity);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        StudentAttendanceRuleEntity entity = getById(id);
        if (entity == null) {
            throw new BusinessException(LanguageConstants.RECORD_NOT_EXIST);
        }
        this.removeById(id);
    }
}