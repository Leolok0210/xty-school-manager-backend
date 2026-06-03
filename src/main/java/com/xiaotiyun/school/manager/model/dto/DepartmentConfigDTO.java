package com.xiaotiyun.school.manager.model.dto;

import lombok.Data;

/**
 * 部门配置DTO
 */
@Data
public class DepartmentConfigDTO {
    
    /**
     * 部门代码
     */
    private String code;
    
    /**
     * 部门名称
     */
    private String name;
    
    /**
     * 学制年数
     */
    private Integer years;
    
    /**
     * 部门类型（如果有则使用此字段，否则使用code）
     */
    private String type;
}
