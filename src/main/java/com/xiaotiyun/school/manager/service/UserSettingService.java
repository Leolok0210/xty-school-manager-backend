package com.xiaotiyun.school.manager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.model.entity.UserSettingEntity;
import com.xiaotiyun.school.manager.model.req.UserSettingReqModel;
import com.xiaotiyun.school.manager.model.res.UserSettingResModel;

/**
 * 用户设置服务接口
 */
public interface UserSettingService extends IService<UserSettingEntity> {

    /**
     * 根据ID获取用户设置
     * @param id 主键ID
     * @return 用户设置信息
     */
    Result<UserSettingResModel> getUserSettingById(Long id);

    /**
     * 创建用户设置
     * @param entity 用户设置实体
     * @return 操作结果
     */
    Result<Boolean> createUserSetting(UserSettingReqModel entity);

    /**
     * 更新用户设置
     * @param entity 用户设置实体
     * @return 操作结果
     */
    Result<Boolean> updateUserSetting(UserSettingReqModel entity);
}

