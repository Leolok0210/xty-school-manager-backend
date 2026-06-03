package com.xiaotiyun.school.manager.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import com.xiaotiyun.school.manager.basic.enums.ResultCode;
import com.xiaotiyun.school.manager.basic.exception.BusinessException;
import com.xiaotiyun.school.manager.basic.exception.BusinessMessageException;
import com.xiaotiyun.school.manager.basic.util.BeanConvertUtil;
import com.xiaotiyun.school.manager.basic.util.DateUtils;
import com.xiaotiyun.school.manager.basic.util.LanguageUtil;
import com.xiaotiyun.school.manager.dao.TeacherAttendanceRuleDao;
import com.xiaotiyun.school.manager.model.entity.DeptEntity;
import com.xiaotiyun.school.manager.model.entity.TeacherAttendanceRule;
import com.xiaotiyun.school.manager.model.entity.UserSchoolRelEntity;
import com.xiaotiyun.school.manager.model.req.TeacherAttendanceRulePageReqModel;
import com.xiaotiyun.school.manager.model.req.TeacherAttendanceRuleSaveReqModel;
import com.xiaotiyun.school.manager.model.res.TeacherAttendanceRuleDepPageResModel;
import com.xiaotiyun.school.manager.model.res.TeacherAttendanceRulePageResModel;
import com.xiaotiyun.school.manager.model.res.TeacherAttendanceRuleUserPageResModel;
import com.xiaotiyun.school.manager.service.DeptService;
import com.xiaotiyun.school.manager.service.TeacherAttendanceRuleService;
import com.xiaotiyun.school.manager.service.UserSchoolRelService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeacherAttendanceRuleServiceImpl extends ServiceImpl<TeacherAttendanceRuleDao, TeacherAttendanceRule> implements TeacherAttendanceRuleService {
    private final DeptService deptService;
    private final UserSchoolRelService userSchoolRelService;
    private final LanguageUtil languageUtil;

    @Override
    public List<Long> selectedUserIds(Long schoolId) {
        QueryWrapper<TeacherAttendanceRule> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(TeacherAttendanceRule::getSchoolId, schoolId)
                .eq(TeacherAttendanceRule::getDeleted, 0);
        List<TeacherAttendanceRule> list = this.list(wrapper);
        if (CollectionUtils.isNotEmpty(list)) {
            List<String> userIdStrs = list.stream().map(TeacherAttendanceRule::getUserIds).collect(Collectors.toList());
            List<Long> userIds = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(userIdStrs)) {
                userIdStrs.forEach(userIdStr -> {
                    userIds.addAll(JSONArray.parseArray(userIdStr).toJavaList(Long.class));
                });
            }
            return userIds;
        }
        return Collections.emptyList();
    }

    @Override
    public PageInfo<TeacherAttendanceRulePageResModel> page(TeacherAttendanceRulePageReqModel reqModel) {
        PageHelper.startPage(reqModel.getPageNum(), reqModel.getPageSize());
        QueryWrapper<TeacherAttendanceRule> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(TeacherAttendanceRule::getSchoolId, reqModel.getSchoolId());
        List<TeacherAttendanceRule> list = this.list(wrapper.lambda().orderByDesc(TeacherAttendanceRule::getCreateTime));
        if (CollectionUtils.isNotEmpty(list)) {
            PageInfo<TeacherAttendanceRule> pageInfo = new PageInfo<>(list);
            List<TeacherAttendanceRulePageResModel> resList = new ArrayList<>();
            for (TeacherAttendanceRule entity : list) {
                TeacherAttendanceRulePageResModel resModel = new TeacherAttendanceRulePageResModel();
                BeanUtils.copyProperties(entity, resModel);
                List<TeacherAttendanceRuleDepPageResModel> depInfos = new ArrayList<>();
                List<TeacherAttendanceRuleUserPageResModel> userInfos = new ArrayList<>();
                List<Integer> effectiveScope = new ArrayList<>();
                if (StringUtils.isNotBlank(entity.getDepIds())) {
                    List<Long> depIds = JSONArray.parseArray(entity.getDepIds()).toJavaList(Long.class);
                    if (CollectionUtils.isNotEmpty(depIds)) {
                        List<DeptEntity> depts = deptService.listByIds(depIds);
                        if (CollectionUtils.isNotEmpty(depts)) {
                            depInfos.addAll(depts.stream()
                                    .map(a -> {
                                        TeacherAttendanceRuleDepPageResModel res = new TeacherAttendanceRuleDepPageResModel();
                                        res.setDepId(a.getId());
                                        res.setName(a.getName());
                                        return res;
                                    }).collect(Collectors.toList()));
                        }
                    }
                }
                if (StringUtils.isNotBlank(entity.getUserIds())) {
                    List<Long> userIds = JSONArray.parseArray(entity.getUserIds()).toJavaList(Long.class);
                    if (CollectionUtils.isNotEmpty(userIds)) {
                        QueryWrapper<UserSchoolRelEntity> queryWrapper = new QueryWrapper<>();
                        queryWrapper.lambda().eq(UserSchoolRelEntity::getSchoolId, reqModel.getSchoolId())
                                .in(UserSchoolRelEntity::getUserId, userIds);
                        List<UserSchoolRelEntity> users = userSchoolRelService.list(queryWrapper);
                        if (CollectionUtils.isNotEmpty(users)) {
                            userInfos.addAll(users.stream()
                                    .map(a -> {
                                        TeacherAttendanceRuleUserPageResModel res = new TeacherAttendanceRuleUserPageResModel();
                                        res.setUserId(a.getUserId());
                                        res.setUserName(a.getUsername());
                                        return res;
                                    }).collect(Collectors.toList()));
                        }
                    }
                }
                if (StringUtils.isNotBlank(entity.getEffectiveScope())) {
                    effectiveScope.addAll(JSONArray.parseArray(entity.getEffectiveScope()).toJavaList(Integer.class));
                }
                resModel.setDepInfos(depInfos);
                resModel.setUserInfos(userInfos);
                resModel.setEffectiveScope(effectiveScope);
                resList.add(resModel);
            }
            PageInfo<TeacherAttendanceRulePageResModel> result = new PageInfo<>(resList);
            result.setTotal(pageInfo.getTotal());
            result.setPages(pageInfo.getPages());
            return result;
        }
        return null;
    }

    @Override
    @Transactional
    public void save(TeacherAttendanceRuleSaveReqModel reqModel) {
        if (!DateUtils.isTimeValid(reqModel.getClockInTime(), reqModel.getClockOutTime())) {
            throw new BusinessException(LanguageConstants.CLOCK_IN_BEFORE_CLOCK_OUT);
        }
        if (!CollectionUtils.isNotEmpty(reqModel.getUserIds()) && !CollectionUtils.isNotEmpty(reqModel.getDepIds())) {
            throw new BusinessMessageException(languageUtil.getMessage(ResultCode.VALIDATE_FAILED.getMessageCode()));
        }
        TeacherAttendanceRule entity = BeanConvertUtil.convert(reqModel, TeacherAttendanceRule.class);
        List<Long> depIds = new ArrayList<>();
        List<Long> userIds = new ArrayList<>();
        List<Integer> effectiveScope = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(reqModel.getDepIds())) {
            depIds.addAll(reqModel.getDepIds());
        }
        if (CollectionUtils.isNotEmpty(reqModel.getUserIds())) {
            userIds.addAll(reqModel.getUserIds());
        }
        if (CollectionUtils.isNotEmpty(reqModel.getEffectiveScope())) {
            effectiveScope.addAll(reqModel.getEffectiveScope());
        }
        entity.setDepIds(JSON.toJSONString(depIds));
        entity.setUserIds(JSON.toJSONString(userIds));
        entity.setEffectiveScope(JSON.toJSONString(effectiveScope));
        this.save(entity);
    }

    @Override
    @Transactional
    public void update(Long id, TeacherAttendanceRuleSaveReqModel reqModel) {
        if (!DateUtils.isTimeValid(reqModel.getClockInTime(), reqModel.getClockOutTime())) {
            throw new BusinessException(LanguageConstants.CLOCK_IN_BEFORE_CLOCK_OUT);
        }
        if (!CollectionUtils.isNotEmpty(reqModel.getUserIds()) && !CollectionUtils.isNotEmpty(reqModel.getDepIds())) {
            throw new BusinessMessageException(languageUtil.getMessage(ResultCode.VALIDATE_FAILED.getMessageCode()));
        }
        TeacherAttendanceRule entity = this.getById(id);
        if (entity != null) {
            BeanUtils.copyProperties(reqModel, entity);
            List<Long> depIds = new ArrayList<>();
            List<Long> userIds = new ArrayList<>();
            List<Integer> effectiveScope = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(reqModel.getDepIds())) {
                depIds.addAll(reqModel.getDepIds());
            }
            if (CollectionUtils.isNotEmpty(reqModel.getUserIds())) {
                userIds.addAll(reqModel.getUserIds());
            }
            if (CollectionUtils.isNotEmpty(reqModel.getEffectiveScope())) {
                effectiveScope.addAll(reqModel.getEffectiveScope());
            }
            entity.setDepIds(JSON.toJSONString(depIds));
            entity.setUserIds(JSON.toJSONString(userIds));
            entity.setEffectiveScope(JSON.toJSONString(effectiveScope));
            this.updateById(entity);
        }
    }

    @Override
    @Transactional
    public void delete(Long id) {
        TeacherAttendanceRule entity = this.getById(id);
        if (entity == null) {
            throw new BusinessException(LanguageConstants.RECORD_NOT_EXIST);
        }
        this.removeById(id);
    }
} 