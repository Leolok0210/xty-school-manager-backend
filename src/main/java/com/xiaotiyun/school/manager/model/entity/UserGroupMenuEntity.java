package com.xiaotiyun.school.manager.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaotiyun.school.manager.basic.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user_group_menu")
public class UserGroupMenuEntity extends BaseEntity {
    /**
     * 用户组ID
     */
    private Long userGroupId;
    
    /**
     * 菜单ID
     */
    private Long menuId;
} 