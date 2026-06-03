package com.xiaotiyun.school.manager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.basic.common.BaseEntity;
import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import com.xiaotiyun.school.manager.basic.enums.ResultCode;
import com.xiaotiyun.school.manager.basic.util.LanguageUtil;
import com.xiaotiyun.school.manager.dao.ExternalCompetitionExportRuleDao;
import com.xiaotiyun.school.manager.model.entity.ExternalCompetitionExportRuleEntity;
import com.xiaotiyun.school.manager.model.req.ExternalCompetitionExportRuleCheckReqModel;
import com.xiaotiyun.school.manager.model.req.ExternalCompetitionExportRuleReqModel;
import com.xiaotiyun.school.manager.model.req.ExternalCompetitionExportRuleSaveReqModel;
import com.xiaotiyun.school.manager.model.res.ExternalCompetitionExportRuleResModel;
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
import java.util.stream.Collectors;

/**
 * 校外活动导出规则表Service实现类
 */
@Service
public class ExternalCompetitionExportRuleServiceImpl extends ServiceImpl<ExternalCompetitionExportRuleDao, ExternalCompetitionExportRuleEntity> implements ExternalCompetitionExportRuleService {

    @Resource
    private LanguageUtil languageUtil;

    @Override
    public PageInfo<ExternalCompetitionExportRuleResModel> pageList(ExternalCompetitionExportRuleReqModel reqModel, Long schoolId) {
        PageHelper.startPage(reqModel.getPageNum(), reqModel.getPageSize());
        QueryWrapper<ExternalCompetitionExportRuleEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("school_id", schoolId);

        if (reqModel.getCategoryId() != null) {
            queryWrapper.eq("category_id", reqModel.getCategoryId());
        }

        if (reqModel.getAwardsId() != null) {
            queryWrapper.eq("awards_id", reqModel.getAwardsId());
        }

        if (reqModel.getType() != null && reqModel.getType() > 0) {
            queryWrapper.eq("type", reqModel.getType());
        }

        if (StringUtils.isNotBlank(reqModel.getRepresentative())) {
            queryWrapper.eq("representative", reqModel.getRepresentative());
        }

        queryWrapper.orderByDesc("create_time");
        List<ExternalCompetitionExportRuleEntity> list = baseMapper.selectList(queryWrapper);

        if (CollectionUtils.isEmpty(list)) {
            return new PageInfo<>(new ArrayList<>());
        }

        List<ExternalCompetitionExportRuleResModel> resModelList = list.stream().map(item -> {
            ExternalCompetitionExportRuleResModel resModel = new ExternalCompetitionExportRuleResModel();
            BeanUtils.copyProperties(item, resModel);
            return resModel;
        }).collect(Collectors.toList());

        return new PageInfo<>(resModelList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Boolean> addOrUpdate(List<ExternalCompetitionExportRuleSaveReqModel> reqModels, Long schoolId) {
        List<ExternalCompetitionExportRuleEntity> insert = new ArrayList<>();
        List<ExternalCompetitionExportRuleEntity> update = new ArrayList<>();
        Map<String, ExternalCompetitionExportRuleEntity> keyRuleMap = new HashMap<>();
        List<String> hasList = new ArrayList<>();

        // 获取老的规则
        List<ExternalCompetitionExportRuleEntity> oldRule = this.list(new QueryWrapper<ExternalCompetitionExportRuleEntity>().eq("school_id", schoolId));
        if (!CollectionUtils.isEmpty(oldRule)) {
            oldRule.forEach(item -> {
                String key = item.getCategoryId() + "-" + item.getAwardsId() + "-" + item.getRepresentative() + "-" + item.getType();
                keyRuleMap.put(key, item);
            });
        }

        for (ExternalCompetitionExportRuleSaveReqModel reqModel : reqModels) {
            // 检查是否重复
            String key = reqModel.getCategoryId()+ "-" + reqModel.getAwardsId() + "-" + reqModel.getRepresentative()+ "-" + reqModel.getType();
            if (keyRuleMap.containsKey(key)) {
                if (reqModel.getId() == null) {
                    return Result.failed(ResultCode.FAILED.getCode(), languageUtil.getMessage(LanguageConstants.EXTERNAL_COMPETITION_EXPORT_RULE_EXISTS));
                } else if (!reqModel.getId().equals(keyRuleMap.get(key).getId())) {
                    return Result.failed(ResultCode.FAILED.getCode(), languageUtil.getMessage(LanguageConstants.EXTERNAL_COMPETITION_EXPORT_RULE_EXISTS));
                }
            }
            if (!hasList.contains(key)) {
                hasList.add(key);
            } else {
                return Result.failed(ResultCode.FAILED.getCode(), languageUtil.getMessage(LanguageConstants.EXTERNAL_COMPETITION_EXPORT_RULE_EXISTS));
            }

            ExternalCompetitionExportRuleEntity entity = new ExternalCompetitionExportRuleEntity();
            BeanUtils.copyProperties(reqModel, entity);
            entity.setSchoolId(schoolId);

            if (reqModel.getId() == null) {
                // 新增
                insert.add(entity);
            } else {
                // 修改
                update.add(entity);
            }
        }

        // 新增
        if (!insert.isEmpty()) {
            this.saveBatch(insert);
        }

        // 修改
        if (!update.isEmpty()) {
            this.updateBatchById(update);
        }

        return Result.success(true);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Boolean> delete(List<Long> ids, Long schoolId) {
        List<ExternalCompetitionExportRuleEntity> list = this.list(new QueryWrapper<ExternalCompetitionExportRuleEntity>()
                .in("id", ids)
                .eq("school_id", schoolId)
        );

        if (CollectionUtils.isEmpty(list)) {
            return Result.success(true);
        }

        this.removeBatchByIds(list.stream().map(BaseEntity::getId).collect(Collectors.toList()));
        return Result.success(true);
    }

    @Override
    public boolean checkRuleExists(ExternalCompetitionExportRuleCheckReqModel reqModel, Long schoolId) {
        QueryWrapper<ExternalCompetitionExportRuleEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("school_id", schoolId);
        queryWrapper.eq("category_id", reqModel.getCategoryId());
        queryWrapper.eq("awards_id", reqModel.getAwardsId());
        queryWrapper.eq("representative", reqModel.getRepresentative());
        queryWrapper.eq("type", reqModel.getType());

        if (reqModel.getId() != null) {
            queryWrapper.ne("id", reqModel.getId());
        }

        return count(queryWrapper) > 0;
    }
}

