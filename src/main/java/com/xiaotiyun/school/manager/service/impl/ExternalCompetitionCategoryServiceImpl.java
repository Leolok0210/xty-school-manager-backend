package com.xiaotiyun.school.manager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import com.xiaotiyun.school.manager.basic.enums.ResultCode;
import com.xiaotiyun.school.manager.basic.util.LanguageUtil;
import com.xiaotiyun.school.manager.dao.ExternalCompetitionCategoryDao;
import com.xiaotiyun.school.manager.model.entity.ExternalCompetitionCategoryEntity;
import com.xiaotiyun.school.manager.model.entity.ExternalCompetitionExportRuleEntity;
import com.xiaotiyun.school.manager.model.req.ExternalCompetitionCategoryReqModel;
import com.xiaotiyun.school.manager.model.req.ExternalCompetitionCategorySaveReqModel;
import com.xiaotiyun.school.manager.model.res.ExternalCompetitionCategoryResModel;
import com.xiaotiyun.school.manager.service.ExternalCompetitionCategoryService;
import com.xiaotiyun.school.manager.service.ExternalCompetitionExportRuleService;
import org.apache.commons.lang3.StringUtils;
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

/**
 * 校外活动范畴表Service实现类
 */
@Service
public class ExternalCompetitionCategoryServiceImpl extends ServiceImpl<ExternalCompetitionCategoryDao, ExternalCompetitionCategoryEntity> implements ExternalCompetitionCategoryService {

    @Resource
    private ExternalCompetitionExportRuleService externalCompetitionExportRuleService;
    @Resource
    private LanguageUtil languageUtil;

    @Override
    public PageInfo<ExternalCompetitionCategoryResModel> pageList(ExternalCompetitionCategoryReqModel reqModel, Long schoolId) {
        PageHelper.startPage(reqModel.getPageNum(), reqModel.getPageSize());
        QueryWrapper<ExternalCompetitionCategoryEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("school_id", schoolId);
        if (StringUtils.isNotBlank(reqModel.getCategoryName())) {
            queryWrapper.like("category_name", reqModel.getCategoryName());
        }
        queryWrapper.orderByDesc("create_time");
        List<ExternalCompetitionCategoryEntity> list = baseMapper.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(list)) {
            return new PageInfo<>(new ArrayList<>());
        }
        List<ExternalCompetitionCategoryResModel> resModelList = list.stream().map(item -> {
            ExternalCompetitionCategoryResModel resModel = new ExternalCompetitionCategoryResModel();
            BeanUtils.copyProperties(item, resModel);
            return resModel;
        }).collect(Collectors.toList());
        return new PageInfo<>(resModelList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Boolean> addOrUpdate(List<ExternalCompetitionCategorySaveReqModel> reqModels, Long schoolId) {
        List<ExternalCompetitionCategoryEntity> insert = new ArrayList<>();
        List<ExternalCompetitionCategoryEntity> update = new ArrayList<>();
        List<String> names = new ArrayList<>();

        // 与数据库对比是否重复
        List<String> newNames = reqModels.stream().map(ExternalCompetitionCategorySaveReqModel::getCategoryName).collect(Collectors.toList());
        List<ExternalCompetitionCategoryEntity> entities = this.list(new QueryWrapper<ExternalCompetitionCategoryEntity>()
                .in("category_name", newNames)
                .eq("school_id", schoolId));
        Map<String, ExternalCompetitionCategoryEntity> nameCategoryMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(entities)){
            nameCategoryMap = entities.stream().collect(Collectors.toMap(ExternalCompetitionCategoryEntity::getCategoryName, Function.identity()));
        }

        for (ExternalCompetitionCategorySaveReqModel reqModel : reqModels) {
            ExternalCompetitionCategoryEntity entity = new ExternalCompetitionCategoryEntity();
            // 检查类型名称是否存在
            if (names.contains(reqModel.getCategoryName())) {
                return Result.failed(ResultCode.FAILED.getCode(), LanguageConstants.EXTERNAL_COMPETITION_CATEGORY_NAME_EXISTS);
            }
            names.add(reqModel.getCategoryName());
            if (reqModel.getId() == null) {
                // 检查类型名称是否存在
                if (nameCategoryMap.containsKey(reqModel.getCategoryName())) {
                    return Result.failed(ResultCode.FAILED.getCode(), LanguageConstants.EXTERNAL_COMPETITION_CATEGORY_NAME_EXISTS);
                }
                // 新增
                entity.setSchoolId(schoolId);
                entity.setCategoryName(reqModel.getCategoryName());
                insert.add(entity);
            } else {
                // 修改
                // 检查类型名称是否存在
                if (nameCategoryMap.containsKey(reqModel.getCategoryName()) &&
                        !nameCategoryMap.get(reqModel.getCategoryName()).getId().equals(reqModel.getId())) {
                    return Result.failed(ResultCode.FAILED.getCode(), LanguageConstants.EXTERNAL_COMPETITION_CATEGORY_NAME_EXISTS);
                }
                entity.setId(reqModel.getId());
                entity.setSchoolId(schoolId);
                entity.setCategoryName(reqModel.getCategoryName());
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
    public Result<Boolean> delete(List<Long> ids, Long schoolId) {
        // 检查规则是否使用本类别
        QueryWrapper<ExternalCompetitionExportRuleEntity> ruleWhere = new QueryWrapper<>();
        ruleWhere.in("category_id", ids)
                .eq("school_id", schoolId);
        if (externalCompetitionExportRuleService.count(ruleWhere) > 0) {
            return Result.failed(ResultCode.FAILED.getCode(), languageUtil.getMessage(LanguageConstants.EXTERNAL_COMPETITION_CATEGORY_USED));
        }
        QueryWrapper<ExternalCompetitionCategoryEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("id", ids)
                .eq("school_id", schoolId);
        boolean result = this.remove(queryWrapper);
        return Result.success(result);
    }

    @Override
    public boolean checkCategoryNameExists(String categoryName, Long schoolId, Long id) {
        QueryWrapper<ExternalCompetitionCategoryEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("category_name", categoryName)
                .eq("school_id", schoolId);
        if (id != null) {
            queryWrapper.ne("id", id);
        }
        return this.count(queryWrapper) > 0;
    }

    @Override
    public List<ExternalCompetitionCategoryResModel> getAllList(Long schoolId) {
        QueryWrapper<ExternalCompetitionCategoryEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("school_id", schoolId)
                .orderByDesc("create_time");
        List<ExternalCompetitionCategoryEntity> list = this.list(queryWrapper);
        if (CollectionUtils.isEmpty(list)) {
            return new ArrayList<>();
        }
        return list.stream().map(item -> {
            ExternalCompetitionCategoryResModel resModel = new ExternalCompetitionCategoryResModel();
            BeanUtils.copyProperties(item, resModel);
            return resModel;
        }).collect(Collectors.toList());
    }
}
