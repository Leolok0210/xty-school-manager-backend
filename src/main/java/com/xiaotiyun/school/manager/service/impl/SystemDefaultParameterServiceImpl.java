package com.xiaotiyun.school.manager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaotiyun.school.manager.basic.common.BaseEntity;
import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import com.xiaotiyun.school.manager.basic.enums.DefaultParamEnum;
import com.xiaotiyun.school.manager.basic.exception.BusinessMessageException;
import com.xiaotiyun.school.manager.basic.util.LanguageUtil;
import com.xiaotiyun.school.manager.dao.SystemDefaultParameterDao;
import com.xiaotiyun.school.manager.helper.DeletePreCheckHelper;
import com.xiaotiyun.school.manager.helper.UpdateAfterOpeHelper;
import com.xiaotiyun.school.manager.model.entity.SystemDefaultParameterEntity;
import com.xiaotiyun.school.manager.model.req.SystemDefaultParameterAddReqModel;
import com.xiaotiyun.school.manager.model.req.SystemDefaultParameterQueryReqModel;
import com.xiaotiyun.school.manager.model.req.SystemDefaultParameterUpdateReqModel;
import com.xiaotiyun.school.manager.model.res.SystemDefaultParameterResModel;
import com.xiaotiyun.school.manager.service.SystemDefaultParameterService;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 系统字典服务实现类
 */
@Service
public class SystemDefaultParameterServiceImpl extends ServiceImpl<SystemDefaultParameterDao, SystemDefaultParameterEntity> implements SystemDefaultParameterService {

    private final DeletePreCheckHelper deletePreCheckHelper;
    private final LanguageUtil languageUtil;
    private final UpdateAfterOpeHelper updateAfterOpeHelper;

    public SystemDefaultParameterServiceImpl(@Lazy DeletePreCheckHelper deletePreCheckHelper, @Lazy UpdateAfterOpeHelper updateAfterOpeHelper, LanguageUtil languageUtil) {
        this.deletePreCheckHelper = deletePreCheckHelper;
        this.updateAfterOpeHelper = updateAfterOpeHelper;
        this.languageUtil = languageUtil;
    }

    /**
     * 根据请求参数查询系统字典列表
     *
     * @param reqModel 请求参数对象
     * @return 包含系统字典列表的结果对象
     */
    @Override
    public Result<Page<SystemDefaultParameterResModel>> listSystemDefaultParameter(SystemDefaultParameterQueryReqModel reqModel) {
        LambdaQueryWrapper<SystemDefaultParameterEntity> where = Wrappers.<SystemDefaultParameterEntity>lambdaQuery()
                .eq(SystemDefaultParameterEntity::getTypeGroup, reqModel.getTypeGroup())
                .eq(SystemDefaultParameterEntity::getSchoolId, reqModel.getSchoolId())
                .like(StringUtils.isNotEmpty(reqModel.getValue()), SystemDefaultParameterEntity::getValue, reqModel.getValue())
                .orderByDesc(BaseEntity::getCreateTime);
        Page<SystemDefaultParameterEntity> page = new Page<>(reqModel.getPageNum(), reqModel.getPageSize());
        page(page, where);
        page.setTotal(count(where));

        List<SystemDefaultParameterResModel> resModels = page.getRecords().stream().map(entity -> {
            SystemDefaultParameterResModel resModel = new SystemDefaultParameterResModel();
            BeanUtils.copyProperties(entity, resModel);
            return resModel;
        }).collect(Collectors.toList());

        Page<SystemDefaultParameterResModel> resPage = new Page<>(reqModel.getPageNum(), reqModel.getPageSize(), page.getTotal());
        resPage.setRecords(resModels);
        return Result.success(resPage);
    }

    /**
     * 添加新的系统字典记录
     *
     * @param addEntity 新的系统字典实体对象
     * @return 操作结果对象
     */
    @Override
    public Result<String> addSystemDefaultParameter(SystemDefaultParameterAddReqModel addEntity, Long schoolId) {
        SystemDefaultParameterEntity entity = new SystemDefaultParameterEntity();
        BeanUtils.copyProperties(addEntity, entity);
        entity.setDeleted(0L);
        entity.setSchoolId(schoolId);
        // 添加前校验值是否重复，编号和值不允许重复
        if (count(Wrappers.<SystemDefaultParameterEntity>lambdaQuery()
                .eq(SystemDefaultParameterEntity::getCode, entity.getCode())
                .eq(SystemDefaultParameterEntity::getSchoolId, entity.getSchoolId())) >0) {
            throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.PRESET_PARAM_CODE_EXISTS));
        }
        if (count(Wrappers.<SystemDefaultParameterEntity>lambdaQuery()
                .eq(SystemDefaultParameterEntity::getValue, entity.getValue())
                .eq(SystemDefaultParameterEntity::getSchoolId, entity.getSchoolId())) > 0){
            throwBusinessException(entity.getTypeGroup());
        }
        save(entity);
        return Result.success();
    }


    /**
     * 更新系统字典记录
     *
     * @param updateEntity 更新的系统字典实体对象
     * @return 操作结果对象
     */
    @Override
    public Result<String> updateSystemDefaultParameter(SystemDefaultParameterUpdateReqModel updateEntity) {
        SystemDefaultParameterEntity old = getById(updateEntity.getId());
        old.setCode(old.getCode() == null? "": old.getCode());
        // 编号和值不允许重复
        if (StringUtils.isNotEmpty(updateEntity.getCode()) && //编号不为空
                !old.getCode().equals(updateEntity.getCode()) && //编号有修改
                count(Wrappers.<SystemDefaultParameterEntity>lambdaQuery() //查询新修改的编号是否已存在
                        .eq(SystemDefaultParameterEntity::getCode,updateEntity.getCode())
                        .eq(SystemDefaultParameterEntity::getSchoolId, old.getSchoolId())) > 0) {
            throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.PRESET_PARAM_CODE_EXISTS));
        }
        if (StringUtils.isNotEmpty(old.getValue()) && //值不为空
                !old.getValue().equals(updateEntity.getValue()) && //值有修改
                count(Wrappers.<SystemDefaultParameterEntity>lambdaQuery() //查询新修改的值是否已存在
                        .eq(SystemDefaultParameterEntity::getValue,updateEntity.getValue())
                        .eq(SystemDefaultParameterEntity::getSchoolId, old.getSchoolId())) > 0) {
            throwBusinessException(old.getTypeGroup());
        }
        // 对比code和value是否修改；code为空时，只对比value
        if ((StringUtils.isEmpty(updateEntity.getCode()) || old.getCode().equals(updateEntity.getCode()))
                && old.getValue().equals(updateEntity.getValue())) {
            return Result.success();
        }

        SystemDefaultParameterEntity entity = new SystemDefaultParameterEntity();
        BeanUtils.copyProperties(updateEntity, entity);
        entity.setTypeGroup(old.getTypeGroup());
        // 更新数据
        if (updateById(entity) && !old.getValue().equals(updateEntity.getValue())){
            // 更新对应类型列表枚举value
            updateAfterOpeHelper.updateSystemDefaultParamAfterOpe(entity);
        }
        return Result.success();
    }

    /**
     * 删除指定ID的系统字典记录
     *
     * @param id 系统字典记录ID
     * @return 操作结果对象
     */
    @Override
    public Result<String> deleteSystemDefaultParameter(Long id) {
        SystemDefaultParameterEntity entity = getById(id);
        if (entity == null) {
            throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.RECORD_NOT_EXIST));
        }
        DefaultParamEnum defaultParamEnum = DefaultParamEnum.getByCode(entity.getTypeGroup());
        // 校验是否存在引用
        if (deletePreCheckHelper.validateBeforeDeleteSysParam(id, defaultParamEnum)) {
            removeById(id);
            return Result.success();
        } else {
            switch (entity.getTypeGroup()){
                case "REST":
                    throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.PRESET_PARAM_REST_VALUE_DELETE));
                case "PERF":
                    throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.PRESET_PARAM_PERF_VALUE_DELETE));
                case "APPEARANCE":
                    throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.PRESET_PARAM_APPEARANCE_NAME_DELETE));
                case "ROUNDS":
                    throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.PRESET_PARAM_ROUNDS_DESC_DELETE));
            }
        }
        return Result.failed();
    }

    private void throwBusinessException(String typeGroup) {
        switch (typeGroup){
            case "REST":
            case "PERF":
                throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.PRESET_PARAM_NAME_EXISTS));
            case "APPEARANCE":
                throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.PRESET_PARAM_DESC_EXISTS));
            case "ROUNDS":
                throw new BusinessMessageException(languageUtil.getMessage(LanguageConstants.PRESET_PARAM_VALUE_EXISTS));
        }
    }
}