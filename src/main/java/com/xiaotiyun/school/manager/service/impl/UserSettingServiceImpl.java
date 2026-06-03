package com.xiaotiyun.school.manager.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.basic.enums.UserSettingEnum;
import com.xiaotiyun.school.manager.dao.UserSettingDao;
import com.xiaotiyun.school.manager.model.entity.UserSettingEntity;
import com.xiaotiyun.school.manager.model.req.UserSettingReqModel;
import com.xiaotiyun.school.manager.model.res.UserSettingResModel;
import com.xiaotiyun.school.manager.service.UserSettingService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * 用户设置服务实现类
 */
@Service
public class UserSettingServiceImpl extends ServiceImpl<UserSettingDao, UserSettingEntity> implements UserSettingService {

    /**
     * 根据ID获取用户设置
     * @param userId 用户ID
     * @return 用户设置信息
     */
    @Override
    public Result<UserSettingResModel> getUserSettingById(Long userId) {
        UserSettingEntity entity = this.getOne(Wrappers.<UserSettingEntity>lambdaQuery()
                .eq(UserSettingEntity::getUserId, userId)
                .eq(UserSettingEntity::getSettingKey, UserSettingEnum.LANGUAGE_TIME.getKey()));
        if (entity == null) {
            return Result.success(null);
        }
        UserSettingResModel resModel = new UserSettingResModel();
        BeanUtils.copyProperties(entity, resModel);
        return Result.success(resModel);
    }

    /**
     * 创建用户设置
     * @param reqModel 用户设置实体
     * @return 操作结果
     */
    @Override
    public Result<Boolean> createUserSetting(UserSettingReqModel reqModel) {
        // 设置参数
        UserSettingEntity entity = new UserSettingEntity();
        entity.setSettingKey(UserSettingEnum.LANGUAGE_TIME.getKey());
        entity.setSettingValue(reqModel.getSettingValue());
        entity.setDescription("用户设置给自己的语言和时间格式");
        entity.setUserId(reqModel.getUserId());
        // 保存实体
        this.save(entity);
        return Result.success();
    }

    /**
     * 更新用户设置
     * @param reqModel 用户设置实体
     * @return 操作结果
     */
    @Override
    public Result<Boolean> updateUserSetting(UserSettingReqModel reqModel) {
        // 检查是否已有配置
        UserSettingEntity entity = this.getOne(Wrappers.<UserSettingEntity>lambdaQuery()
                .eq(UserSettingEntity::getUserId, reqModel.getUserId())
                .eq(UserSettingEntity::getSettingKey, UserSettingEnum.LANGUAGE_TIME.getKey()));
        if (entity == null) {
            // 设置参数
            entity = new UserSettingEntity();
            entity.setSettingKey(UserSettingEnum.LANGUAGE_TIME.getKey());
            entity.setSettingValue(reqModel.getSettingValue());
            entity.setDescription("用户设置给自己的语言和时间格式");
            entity.setUserId(reqModel.getUserId());
            // 保存实体
            this.save(entity);
        } else {
            entity.setSettingValue(reqModel.getSettingValue());
            // 更新实体
            this.updateById(entity);
        }
        return Result.success();

    }

}

