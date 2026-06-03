package com.xiaotiyun.school.manager.model.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 系统设置历史配置DTO
 * 用于承载CTE查询结果，包含时间窗口信息
 */
@Data
public class SystemSettingHistoryDTO {
    
    /**
     * 主键ID
     */
    private Long id;
    
    /**
     * 学校ID
     */
    private Long schoolId;
    
    /**
     * 配置项键名
     */
    private String settingKey;
    
    /**
     * 新配置值
     */
    private String newValue;
    
    /**
     * 配置生效开始时间
     */
    private LocalDateTime startTime;
    
    /**
     * 配置生效结束时间
     */
    private LocalDateTime endTime;
    
    /**
     * 操作人ID
     */
    private Long operatorId;
    
    /**
     * 操作人姓名
     */
    private String operatorName;
}
