package com.xiaotiyun.school.manager.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaotiyun.school.manager.basic.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_menu")
public class MenuEntity extends BaseEntity {
    
    /**
     * 类型(1:菜单,2:按钮)
     */
    private Integer type;
    
    /**
     * 上级菜单ID
     */
    private Long parentId;
    
    /**
     * 菜单名称
     */
    private String menuName;
    
    /**
     * 路由路径
     */
    private String routePath;
    
    /**
     * 组件路径
     */
    private String componentPath;
    
    /**
     * 权限标识
     */
    private String permission;
    
    /**
     * 图标
     */
    private String icon;
    
    /**
     * 排序值
     */
    private Integer sort;
    
    /**
     * 状态(0:隐藏,1:展示)
     */
    private Integer status;
} 