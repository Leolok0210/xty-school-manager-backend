package com.xiaotiyun.school.manager.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaotiyun.school.manager.basic.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统设置历史记录实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("system_setting_history")
public class SystemSettingHistoryEntity extends BaseEntity {
    
    /**
     * 学校ID
     */
    private Long schoolId;
    
    /**
     * 配置项键名
     */
    private String settingKey;
    
    /**
     * 原配置值
     */
    private String oldValue;
    
    /**
     * 新配置值
     */
    private String newValue;
    
    /**
     * 操作人ID
     */
    private Long operatorId;
    
    /**
     * 操作人姓名
     */
    private String operatorName;
} 