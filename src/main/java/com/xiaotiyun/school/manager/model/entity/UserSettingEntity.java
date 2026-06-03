package com.xiaotiyun.school.manager.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaotiyun.school.manager.basic.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户设置实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user_setting")
public class UserSettingEntity extends BaseEntity {
    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 配置项键名
     */
    private String settingKey;

    /**
     * 配置项值
     */
    private String settingValue;

    /**
     * 配置项描述
     */
    private String description;
}

