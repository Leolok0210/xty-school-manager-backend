package com.xiaotiyun.school.manager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.basic.common.BaseEntity;
import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import com.xiaotiyun.school.manager.basic.enums.ResultCode;
import com.xiaotiyun.school.manager.dao.StudentUsuallyTypeDao;
import com.xiaotiyun.school.manager.model.entity.StudentUsuallyRuleEntity;
import com.xiaotiyun.school.manager.model.entity.StudentUsuallyTypeEntity;
import com.xiaotiyun.school.manager.model.req.StudentUsuallyTypeReqModel;
import com.xiaotiyun.school.manager.model.req.StudentUsuallyTypeSaveReqModel;
import com.xiaotiyun.school.manager.model.res.StudentUsuallyTypeResModel;
import com.xiaotiyun.school.manager.service.StudentUsuallyRuleService;
import com.xiaotiyun.school.manager.service.StudentUsuallyTypeService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 平时成绩类型Service实现类
 */
@Service
public class StudentUsuallyTypeServiceImpl extends ServiceImpl<StudentUsuallyTypeDao, StudentUsuallyTypeEntity> implements StudentUsuallyTypeService {

    @Resource
    private StudentUsuallyRuleService studentUsuallyRuleService;

    @Override
    public PageInfo<StudentUsuallyTypeResModel> pageList(StudentUsuallyTypeReqModel reqModel, Long schoolId) {
        PageHelper.startPage(reqModel.getPageNum(), reqModel.getPageSize());
        QueryWrapper<StudentUsuallyTypeEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("school_id", schoolId);
        if (StringUtils.isNotBlank(reqModel.getTypeName())) {
            queryWrapper.like("type_name", reqModel.getTypeName());
        }
        queryWrapper.orderByDesc("create_time");
        List<StudentUsuallyTypeEntity> list = baseMapper.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(list)) {
            return new PageInfo<>(new ArrayList<>());
        }
        List<StudentUsuallyTypeResModel> resModelList = list.stream().map(item -> {
            StudentUsuallyTypeResModel resModel = new StudentUsuallyTypeResModel();
            BeanUtils.copyProperties(item, resModel);
            return resModel;
        }).collect(Collectors.toList());
        return new PageInfo<>(resModelList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Boolean> addOrUpdate(List<StudentUsuallyTypeSaveReqModel> reqModels, Long schoolId) {
        List<StudentUsuallyTypeEntity> insert = new ArrayList<>();
        List<StudentUsuallyTypeEntity> update = new ArrayList<>();
        List<String> names = new ArrayList<>();
        // 与数据库对比是否重复
        List<String> newNames = reqModels.stream().map(StudentUsuallyTypeSaveReqModel::getTypeName).collect(Collectors.toList());
        List<StudentUsuallyTypeEntity> entities = this.list(new QueryWrapper<StudentUsuallyTypeEntity>()
                .in("type_name", newNames)
                .eq("school_id", schoolId));
        if (!CollectionUtils.isEmpty(entities)){
            return Result.failed(ResultCode.FAILED.getCode(), LanguageConstants.STUDENT_USUALLY_TYPE_NAME_EXISTS);
        }
        for (StudentUsuallyTypeSaveReqModel reqModel : reqModels) {
            StudentUsuallyTypeEntity entity = new StudentUsuallyTypeEntity();
            // 检查类型名称是否存在
            if (names.contains(reqModel.getTypeName())) {
                return Result.failed(ResultCode.FAILED.getCode(), LanguageConstants.STUDENT_USUALLY_TYPE_NAME_EXISTS);
            }
            names.add(reqModel.getTypeName());
            if (reqModel.getId() == null) {
                // 新增
                entity.setSchoolId(schoolId);
                entity.setTypeName(reqModel.getTypeName());
                insert.add(entity);
            } else {
                // 修改
                entity.setId(reqModel.getId());
                entity.setSchoolId(schoolId);
                entity.setTypeName(reqModel.getTypeName());
                update.add(entity);
            }
        }
        // 新增
        if (!insert.isEmpty()){
            this.saveBatch(insert);
        }
        // 修改
        if (!update.isEmpty()){
            this.updateBatchById(update);
        }
        return Result.success(true);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Boolean> delete(List<Long> ids, Long schoolId) {
        // 检查是否被使用
        List<StudentUsuallyRuleEntity> ruleEntities = studentUsuallyRuleService.list(new QueryWrapper<StudentUsuallyRuleEntity>()
                .in("type_id", ids)
                .eq("school_id", schoolId));
        if (!CollectionUtils.isEmpty(ruleEntities)) {
            return Result.failed(ResultCode.FAILED.getCode(), LanguageConstants.STUDENT_USUALLY_TYPE_USED);
        }
        List<StudentUsuallyTypeEntity> list = this.list(new QueryWrapper<StudentUsuallyTypeEntity>()
                .in("id", ids)
                .eq("school_id", schoolId));
        if (CollectionUtils.isEmpty(list)) {
            return Result.success(true);
        }
        this.removeBatchByIds(list.stream().map(BaseEntity::getId).collect(Collectors.toList()));
        return Result.success(true);
    }

    @Override
    public boolean checkTypeNameExists(String typeName, Long schoolId, Long id) {
        QueryWrapper<StudentUsuallyTypeEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("school_id", schoolId);
        queryWrapper.eq("type_name", typeName);
        queryWrapper.eq("deleted", 0);
        if (id != null) {
            queryWrapper.ne("id", id);
        }
        return count(queryWrapper) > 0;
    }
}
