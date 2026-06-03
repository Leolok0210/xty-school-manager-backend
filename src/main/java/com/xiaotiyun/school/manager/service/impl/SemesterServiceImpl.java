package com.xiaotiyun.school.manager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaotiyun.school.manager.basic.exception.BusinessException;
import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import com.xiaotiyun.school.manager.basic.exception.BusinessMessageException;
import com.xiaotiyun.school.manager.basic.util.LanguageUtil;
import com.xiaotiyun.school.manager.dao.SemesterDao;
import com.xiaotiyun.school.manager.helper.DeletePreCheckHelper;
import com.xiaotiyun.school.manager.model.entity.GradeGroup;
import com.xiaotiyun.school.manager.model.entity.SemesterEntity;
import com.xiaotiyun.school.manager.model.req.SemesterAddReqModel;
import com.xiaotiyun.school.manager.model.req.SemesterQueryReqModel;
import com.xiaotiyun.school.manager.model.res.SemesterResModel;
import com.xiaotiyun.school.manager.service.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.context.annotation.Lazy;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SemesterServiceImpl extends ServiceImpl<SemesterDao, SemesterEntity> implements SemesterService {
    @Resource
    private GradeGroupService groupService;

    private final DeletePreCheckHelper deletePreCheckHelper;

    public SemesterServiceImpl(@Lazy DeletePreCheckHelper deletePreCheckHelper) {
        this.deletePreCheckHelper = deletePreCheckHelper;
    }

    @Resource
    private LanguageUtil languageUtil;
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchSave(List<SemesterAddReqModel> reqModels, Long schoolId) {
        // 1. 参数校验
        if (CollectionUtils.isEmpty(reqModels)) {
            return;
        }
        if (schoolId == null) {
            throw new BusinessException(LanguageConstants.SCHOOL_ID_REQUIRED);
        }

        // 2. 数据分组 - 只获取需要更新的数据进行校验
        List<SemesterAddReqModel> updateList = reqModels.stream()
                .filter(model -> model.getId() != null)
                .collect(Collectors.toList());

        // 3. 更新数据校验
        if (!updateList.isEmpty()) {
            List<Long> updateIds = updateList.stream()
                    .map(SemesterAddReqModel::getId)
                    .collect(Collectors.toList());
            
            List<SemesterEntity> existingSemesters = this.listByIds(updateIds);
            
            // 检查是否所有ID都存在
            if (existingSemesters.size() != updateIds.size()) {
                Set<Long> existingIds = existingSemesters.stream()
                        .map(SemesterEntity::getId)
                        .collect(Collectors.toSet());
                List<Long> notFoundIds = updateIds.stream()
                        .filter(id -> !existingIds.contains(id))
                        .collect(Collectors.toList());
                throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.SEMESTER_ID_NOT_EXISTS) + ": " +
                        String.join(",", notFoundIds.stream().map(String::valueOf).collect(Collectors.toList())));
            }
            
            // 检查学校权限
            List<Long> unauthorizedIds = existingSemesters.stream()
                    .filter(semester -> !schoolId.equals(semester.getSchoolId()))
                    .map(SemesterEntity::getId)
                    .collect(Collectors.toList());
            if (!unauthorizedIds.isEmpty()) {
                throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.NO_PERMISSION_OPERATE_SEMESTER)+ ": " +
                        String.join(",", unauthorizedIds.stream().map(String::valueOf).collect(Collectors.toList())));
            }
        }

        // 4. 时间重叠校验
        // 4.1 检查请求数据内部的时间重叠
        for (int i = 0; i < reqModels.size(); i++) {
            SemesterAddReqModel current = reqModels.get(i);
            for (int j = i + 1; j < reqModels.size(); j++) {
                SemesterAddReqModel other = reqModels.get(j);
                if (current.getDepartment().equals(other.getDepartment()) &&
                    isTimeOverlap(current.getStartTime(), current.getEndTime(),
                                other.getStartTime(), other.getEndTime())) {
                    throw new BusinessMessageException(String.format(languageUtil.getMessage(LanguageConstants.SEMESTER_TIME_OVERLAP), current.getName(), other.getName()));
                }
            }
        }

        // 4.2 检查与数据库已存在记录的时间重叠
        for (SemesterAddReqModel reqModel : reqModels) {
            LambdaQueryWrapper<SemesterEntity> wrapper = new LambdaQueryWrapper<SemesterEntity>()
                    .eq(SemesterEntity::getSchoolId, schoolId)
                    .eq(SemesterEntity::getDepartment, reqModel.getDepartment())
                    .eq(SemesterEntity::getDeleted, 0)  // 添加未删除条件
                    .ne(reqModel.getId()!=null,SemesterEntity::getId,reqModel.getId())
                    .and(w -> w
                            .between(SemesterEntity::getStartTime, reqModel.getStartTime(), reqModel.getEndTime())
                            .or()
                            .between(SemesterEntity::getEndTime, reqModel.getStartTime(), reqModel.getEndTime())
                            .or()
                            .and(sw -> sw
                                    .le(SemesterEntity::getStartTime, reqModel.getStartTime())
                                    .ge(SemesterEntity::getEndTime, reqModel.getEndTime())));
            
            if (reqModel.getId() != null) {
                wrapper.ne(SemesterEntity::getId, reqModel.getId());
            }
            
            List<SemesterEntity> overlappingSemesters = this.list(wrapper);
            if (!overlappingSemesters.isEmpty()) {
                String overlappingSemesterNames = overlappingSemesters.stream()
                        .map(SemesterEntity::getName)
                        .collect(Collectors.joining(","));
                throw new BusinessMessageException(String.format(languageUtil.getMessage(LanguageConstants.SEMESTER_TIME_OVERLAP), reqModel.getName(), overlappingSemesterNames));
            }
        }

        // 5. 批量保存和更新
        List<SemesterEntity> entities = reqModels.stream().map(reqModel -> {
            SemesterEntity entity = new SemesterEntity();
            BeanUtils.copyProperties(reqModel, entity);
            entity.setSchoolId(schoolId);
            return entity;
        }).collect(Collectors.toList());
        
        this.saveOrUpdateBatch(entities);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id, Long schoolId) {
        // 1. 校验学段是否存在
        SemesterEntity semester = getById(id);
        if (semester == null || !semester.getSchoolId().equals(schoolId)) {
            throw new BusinessException(LanguageConstants.SEMESTER_NOT_EXISTS);
        }

        // 2. 检查是否有关联的成绩数据
        if(deletePreCheckHelper.validateBeforeDeleteSemester(id)) {
            throw new BusinessException(LanguageConstants.SEMESTER_HAS_SCORE);
        }

        // 3. 逻辑删除
        this.removeById(id);
    }

    @Override
    public List<SemesterResModel> list(SemesterQueryReqModel reqModel, Long schoolId) {
        // 1. 构建查询条件
        LambdaQueryWrapper<SemesterEntity> wrapper = new LambdaQueryWrapper<SemesterEntity>()
                .eq(SemesterEntity::getSchoolId, schoolId)
                .eq(StringUtils.isNotBlank(reqModel.getSchoolYear()), 
                    SemesterEntity::getSchoolYear, reqModel.getSchoolYear())
                .orderByAsc(SemesterEntity::getDepartment)
                .orderByAsc(SemesterEntity::getStartTime);

        // 2. 查询数据
        List<SemesterEntity> list = list(wrapper);

        // 3. 转换返回结果
        return list.stream().map(entity -> {
            SemesterResModel resModel = new SemesterResModel();
            BeanUtils.copyProperties(entity, resModel);
            return resModel;
        }).collect(Collectors.toList());
    }

    @Override
    public List<SemesterResModel> listByStudent(SemesterQueryReqModel reqModel, Long schoolId) {
        // 查询级组
        GradeGroup group = groupService.getById(reqModel.getGroupId());
        if (group == null) {
            throw new BusinessException(LanguageConstants.PARAM_ERROR);
        }

        // 1. 构建查询条件
        LambdaQueryWrapper<SemesterEntity> wrapper = new LambdaQueryWrapper<SemesterEntity>()
                .eq(SemesterEntity::getSchoolId, schoolId)
                .eq(StringUtils.isNotBlank(reqModel.getSchoolYear()),
                        SemesterEntity::getSchoolYear, reqModel.getSchoolYear())
                .eq(SemesterEntity::getDepartment, group.getDepartment())
                .orderByAsc(SemesterEntity::getDepartment)
                .orderByAsc(SemesterEntity::getStartTime);

        // 2. 查询数据
        List<SemesterEntity> list = list(wrapper);

        // 3. 转换返回结果
        return list.stream().map(entity -> {
            SemesterResModel resModel = new SemesterResModel();
            BeanUtils.copyProperties(entity, resModel);
            return resModel;
        }).collect(Collectors.toList());
    }

    /**
     * 判断两个时间段是否重叠
     * @param start1 时间段1的开始时间
     * @param end1 时间段1的结束时间
     * @param start2 时间段2的开始时间
     * @param end2 时间段2的结束时间
     * @return 是否重叠
     */
    private boolean isTimeOverlap(LocalDateTime start1, LocalDateTime end1, 
                                LocalDateTime start2, LocalDateTime end2) {
        return !(end1.isBefore(start2) || start1.isAfter(end2));
    }

    @Override
    public Map<Long, String> getNamesByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return new HashMap<>();
        }
        
        // 查询学段信息
        List<SemesterEntity> semesters = baseMapper.selectList(
                new LambdaQueryWrapper<SemesterEntity>()
                        .in(SemesterEntity::getId, ids)
                        .eq(SemesterEntity::getDeleted, 0));
        
        // 转换为Map
        return semesters.stream()
                .collect(Collectors.toMap(
                        SemesterEntity::getId,
                        SemesterEntity::getName,
                        (v1, v2) -> v1));  // 如果有重复key,保留第一个值
    }
}