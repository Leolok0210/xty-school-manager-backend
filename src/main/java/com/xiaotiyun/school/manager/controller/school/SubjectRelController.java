package com.xiaotiyun.school.manager.controller.school;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.xiaotiyun.school.manager.basic.common.BasicController;
import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import com.xiaotiyun.school.manager.basic.exception.BusinessException;
import com.xiaotiyun.school.manager.helper.SubjectCheckHelper;
import com.xiaotiyun.school.manager.model.entity.SubjectRelEntity;
import com.xiaotiyun.school.manager.model.req.SubjectRelQueryReqModel;
import com.xiaotiyun.school.manager.service.SubjectRelService;
import com.xiaotiyun.school.manager.service.SubjectService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.xiaotiyun.school.manager.model.req.SubjectRelReqModel;
import com.xiaotiyun.school.manager.model.res.SubjectRelResModel;
import com.xiaotiyun.school.manager.model.req.SubjectRelBatchUpdateNumberReqModel;
import com.xiaotiyun.school.manager.model.res.SubjectDetailResModel;
import com.xiaotiyun.school.manager.model.req.SubjectRelGroupQueryReqModel;


import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Api(tags = "科目表关联管理")
@RestController
@RequestMapping("/api/grade/subject-rel")
@Validated
public class SubjectRelController extends BasicController {

    @Resource
    private SubjectRelService subjectRelService;
    @Resource
    private SubjectService subjectService;

    @Resource
    private SubjectCheckHelper subjectCheckHelper;

    @ApiOperation("批量新增科目关联")
    @SaCheckPermission("grade:subject-rel:add")
    @PostMapping("/add")
    public Result<Void> addBatch(HttpServletRequest request, @Valid @RequestBody List<SubjectRelReqModel> reqList) {
        List<SubjectRelEntity> entityList = reqList.stream().map(req -> {
            SubjectRelEntity entity = new SubjectRelEntity();
            entity.setGroupId(req.getGroupId());
            entity.setSubjectId(req.getSubjectId());
            entity.setNumber(req.getNumber());
            entity.setCountedInAverage(req.getCountedInAverage());
            entity.setArtsScience(req.getArtsScience());
            entity.setSubjectType(req.getSubjectType());
            entity.setSchoolId(req.getSchoolId());
            entity.setShowRule(0);
            entity.setCreateTime(LocalDateTime.now());
            entity.setUpdateTime(LocalDateTime.now());
            entity.setDeleted(0L);
            return entity;
        }).collect(Collectors.toList());
        //1. 同一个科目一个年级下，只能被添加1次；已经被选择的科目，不可再次被选择
        List<Long> subjectIds = entityList.stream().map(SubjectRelEntity::getSubjectId).collect(Collectors.toList());
        List<Long> groupIds = entityList.stream().map(SubjectRelEntity::getGroupId).collect(Collectors.toList());
        LambdaQueryWrapper<SubjectRelEntity> subjectRelEntityLambdaQueryWrapper = new LambdaQueryWrapper<>();
        subjectRelEntityLambdaQueryWrapper.in(SubjectRelEntity::getSubjectId, subjectIds);
        subjectRelEntityLambdaQueryWrapper.in(SubjectRelEntity::getGroupId, groupIds);
        subjectRelEntityLambdaQueryWrapper.eq(SubjectRelEntity::getSchoolId, getSchoolId());
        List<SubjectRelEntity> existList = subjectRelService.list(subjectRelEntityLambdaQueryWrapper);
        if (!existList.isEmpty()) {
            HashSet<Long> subMap = new HashSet<>(subjectIds);
            List<Long> existSubjectIds = existList.stream().map(SubjectRelEntity::getSubjectId).collect(Collectors.toList());
            if (subMap.containsAll(existSubjectIds)) {
                throw new BusinessException(LanguageConstants.SUBJECT_EXISTS_IN_GRADE);
            }
        }
        subjectRelService.saveBatch(entityList);
        return Result.success();
    }

    @ApiOperation("修改科目关联")
    @SaCheckPermission("grade:subject-rel:update")
    @PostMapping("/update")
    public Result<Void> update(@Valid @RequestBody SubjectRelReqModel req) {
        SubjectRelEntity entity = new SubjectRelEntity();
        entity.setGroupId(req.getGroupId());
        entity.setSubjectId(req.getSubjectId());
        entity.setNumber(req.getNumber());
        entity.setCountedInAverage(req.getCountedInAverage());
        entity.setArtsScience(req.getArtsScience());
        entity.setSubjectType(req.getSubjectType());
        entity.setSchoolId(req.getSchoolId());
        entity.setUpdateTime(LocalDateTime.now());
        entity.setId(req.getId());
        subjectRelService.updateById(entity);
        return Result.success();
    }

    @ApiOperation("删除科目关联")
    @SaCheckPermission("grade:subject-rel:delete")
    @GetMapping("/delete")
    public Result<Void> delete(@RequestParam Long id) {
        boolean checkSubject = subjectCheckHelper.checkSubject(getSchoolId(), id);
        if (!checkSubject) {
            throw new BusinessException(LanguageConstants.SUBJECT_HAS_DATA);
        }
        SubjectRelEntity subjectRel = subjectRelService.getById(id);
        subjectRelService.removeById(id);
        //更新这个下面的全部数据的顺序-1
        LambdaUpdateWrapper<SubjectRelEntity> subjectRelEntityLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        subjectRelEntityLambdaUpdateWrapper.eq(SubjectRelEntity::getGroupId, subjectRel.getGroupId())
                .gt(SubjectRelEntity::getNumber, subjectRel.getNumber())
                .eq(SubjectRelEntity::getSchoolId, subjectRel.getSchoolId())
                .setSql("number = number - 1"); // 更规范方式
        subjectRelService.update(subjectRelEntityLambdaUpdateWrapper);
        return Result.success();
    }

    @ApiOperation("批量修改科目关联序号")
    @SaCheckPermission("grade:subject-rel:update")
    @PostMapping("/batch-update-number")
    public Result<Void> batchUpdateNumber(@Valid @RequestBody List<SubjectRelBatchUpdateNumberReqModel> reqList) {
        List<SubjectRelEntity> updateList = reqList.stream().map(req -> {
            SubjectRelEntity entity = new SubjectRelEntity();
            entity.setId(req.getId());
            entity.setNumber(req.getNumber());
            entity.setUpdateTime(LocalDateTime.now());
            return entity;
        }).collect(Collectors.toList());
        subjectRelService.updateBatchById(updateList);
        return Result.success();
    }

    @ApiOperation("查询科目关联详情")
    @SaCheckPermission("grade:subject-rel:get")
    @GetMapping("/get")
    public Result<SubjectRelResModel> get(@Valid SubjectRelQueryReqModel reqModel) {
        LambdaQueryWrapper<SubjectRelEntity> wrapper = new LambdaQueryWrapper<>();
        if (reqModel.getId() != null) {
            wrapper.eq(SubjectRelEntity::getId, reqModel.getId());
        }
        if (reqModel.getGroupId() != null) {
            wrapper.eq(SubjectRelEntity::getGroupId, reqModel.getGroupId());
        }
        if (reqModel.getSubjectId() != null) {
            wrapper.eq(SubjectRelEntity::getSubjectId, reqModel.getSubjectId());
        }
        if (reqModel.getSchoolId() != null) {
            wrapper.eq(SubjectRelEntity::getSchoolId, reqModel.getSchoolId());
        }
        SubjectRelEntity entity = subjectRelService.getOne(wrapper);
        if (entity == null) {
            return Result.success(null);
        }
        SubjectRelResModel resModel = new SubjectRelResModel();
        BeanUtils.copyProperties(entity, resModel);
        if (entity.getSubjectId() != null) {
            List<SubjectDetailResModel> subjects = subjectService.getSubjects(Lists.newArrayList(entity.getSubjectId()));
            if (subjects != null) {
                resModel.setSubject(subjects.get(0));
            }
        }
        return Result.success(resModel);
    }

    @ApiOperation("根据学校id和级组id查询科目信息和关联信息")
    @SaCheckPermission("grade:subject-rel:list")
    @PostMapping("/list-by-group")
    public Result<List<SubjectRelResModel>> listByGroup(@Valid @RequestBody SubjectRelGroupQueryReqModel reqModel) {
        List<SubjectRelResModel> resList = subjectRelService.listByGroup(reqModel);
        return Result.success(resList);
    }

    @ApiOperation("根据学校id和级组id查询科目信息和关联信息-学生端(非鉴权)")
    @PostMapping("/student/list")
    public Result<List<SubjectRelResModel>> listByGroupByStudent(@Valid @RequestBody SubjectRelGroupQueryReqModel reqModel) {
        List<SubjectRelResModel> resList = subjectRelService.listByGroup(reqModel);
        return Result.success(resList);
    }
} 