package com.xiaotiyun.school.manager.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaotiyun.school.manager.basic.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统设置实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("system_setting")
public class SystemSettingEntity extends BaseEntity {
    
    /**
     * 学校ID
     */
    private Long schoolId;
    
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
    
    /**
     * 操作人ID
     */
    private Long operatorId;
    
    /**
     * 操作人姓名 
     */
    private String operatorName;
}