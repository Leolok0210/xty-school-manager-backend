package com.xiaotiyun.school.manager.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaotiyun.school.manager.basic.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_school_menu")
public class SchoolMenuEntity extends BaseEntity {
    /**
     * 学校ID
     */
    private Long schoolId;
    
    /**
     * 菜单ID
     */
    private Long menuId;
} 