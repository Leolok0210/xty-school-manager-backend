package com.xiaotiyun.school.manager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import com.xiaotiyun.school.manager.basic.enums.ResultCode;
import com.xiaotiyun.school.manager.basic.util.LanguageUtil;
import com.xiaotiyun.school.manager.dao.ExternalCompetitionAwardsDao;
import com.xiaotiyun.school.manager.model.entity.ExternalCompetitionAwardsEntity;
import com.xiaotiyun.school.manager.model.entity.ExternalCompetitionExportRuleEntity;
import com.xiaotiyun.school.manager.model.req.ExternalCompetitionAwardsReqModel;
import com.xiaotiyun.school.manager.model.req.ExternalCompetitionAwardsSaveReqModel;
import com.xiaotiyun.school.manager.model.res.ExternalCompetitionAwardsResModel;
import com.xiaotiyun.school.manager.service.ExternalCompetitionAwardsService;
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
 * 校外活动奖项评级Service实现类
 */
@Service
public class ExternalCompetitionAwardsServiceImpl extends ServiceImpl<ExternalCompetitionAwardsDao, ExternalCompetitionAwardsEntity> implements ExternalCompetitionAwardsService {

    @Resource
    private ExternalCompetitionExportRuleServiceImpl externalCompetitionExportRuleService;
    @Resource
    private LanguageUtil languageUtil;


    @Override
    public PageInfo<ExternalCompetitionAwardsResModel> pageList(ExternalCompetitionAwardsReqModel reqModel, Long schoolId) {
        PageHelper.startPage(reqModel.getPageNum(), reqModel.getPageSize());
        QueryWrapper<ExternalCompetitionAwardsEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("school_id", schoolId);
        if (StringUtils.isNotBlank(reqModel.getAwardsName())) {
            queryWrapper.like("awards_name", reqModel.getAwardsName());
        }
        queryWrapper.orderByDesc("create_time");
        List<ExternalCompetitionAwardsEntity> list = baseMapper.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(list)) {
            return new PageInfo<>(new ArrayList<>());
        }
        List<ExternalCompetitionAwardsResModel> resModelList = list.stream().map(item -> {
            ExternalCompetitionAwardsResModel resModel = new ExternalCompetitionAwardsResModel();
            BeanUtils.copyProperties(item, resModel);
            return resModel;
        }).collect(Collectors.toList());
        return new PageInfo<>(resModelList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Boolean> addOrUpdate(List<ExternalCompetitionAwardsSaveReqModel> reqModels, Long schoolId) {
        List<ExternalCompetitionAwardsEntity> insert = new ArrayList<>();
        List<ExternalCompetitionAwardsEntity> update = new ArrayList<>();
        List<String> names = new ArrayList<>();

        // 与数据库对比是否重复
        List<String> newNames = reqModels.stream().map(ExternalCompetitionAwardsSaveReqModel::getAwardsName).collect(Collectors.toList());
        List<ExternalCompetitionAwardsEntity> entities = this.list(new QueryWrapper<ExternalCompetitionAwardsEntity>()
                .in("awards_name", newNames)
                .eq("school_id", schoolId));
        Map<String, ExternalCompetitionAwardsEntity> nameAwardsMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(entities)){
            nameAwardsMap = entities.stream().collect(Collectors.toMap(ExternalCompetitionAwardsEntity::getAwardsName, Function.identity()));
        }

        for (ExternalCompetitionAwardsSaveReqModel reqModel : reqModels) {
            ExternalCompetitionAwardsEntity entity = new ExternalCompetitionAwardsEntity();
            // 检查类型名称是否存在
            if (names.contains(reqModel.getAwardsName())) {
                return Result.failed(ResultCode.FAILED.getCode(), LanguageConstants.EXTERNAL_COMPETITION_AWARDS_NAME_EXISTS);
            }
            names.add(reqModel.getAwardsName());

            if (reqModel.getId() == null) {
                // 新增
                if (nameAwardsMap.containsKey(reqModel.getAwardsName())){
                    return Result.failed(ResultCode.FAILED.getCode(), LanguageConstants.EXTERNAL_COMPETITION_AWARDS_NAME_EXISTS);
                }
                entity.setSchoolId(schoolId);
                entity.setAwardsName(reqModel.getAwardsName());
                insert.add(entity);
            } else {
                // 修改
                if (nameAwardsMap.containsKey(reqModel.getAwardsName()) &&
                        !nameAwardsMap.get(reqModel.getAwardsName()).getId().equals(reqModel.getId())){
                    return Result.failed(ResultCode.FAILED.getCode(), LanguageConstants.EXTERNAL_COMPETITION_AWARDS_NAME_EXISTS);
                }
                entity.setId(reqModel.getId());
                entity.setSchoolId(schoolId);
                entity.setAwardsName(reqModel.getAwardsName());
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
        if (CollectionUtils.isEmpty(ids)) {
            return Result.success(true);
        }
        // 检查是否被使用
        QueryWrapper<ExternalCompetitionExportRuleEntity> ruleWhere = new QueryWrapper<>();
        ruleWhere.in("awards_id", ids)
                .eq("school_id", schoolId);
        if (externalCompetitionExportRuleService.count(ruleWhere) > 0) {
            return Result.failed(ResultCode.FAILED.getCode(), languageUtil.getMessage(LanguageConstants.EXTERNAL_COMPETITION_AWARDS_USED));
        }
        // 逻辑删除
        this.remove(new QueryWrapper<ExternalCompetitionAwardsEntity>()
                .in("id", ids)
                .eq("school_id", schoolId));
        return Result.success(true);
    }

    @Override
    public boolean checkAwardsNameExists(String awardsName, Long schoolId, Long id) {
        QueryWrapper<ExternalCompetitionAwardsEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("school_id", schoolId);
        queryWrapper.eq("awards_name", awardsName);
        if (id != null) {
            queryWrapper.ne("id", id);
        }
        queryWrapper.eq("deleted", 0);
        return baseMapper.exists(queryWrapper);
    }
}
