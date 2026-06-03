package com.xiaotiyun.school.manager.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.xiaotiyun.school.manager.basic.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户部门关联表 Entity
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("sys_user_dept_rel")
public class UserDeptRelEntity extends BaseEntity {

    /**
     * 学校ID
     */
    @TableField("school_id")
    private Long schoolId;

    /**
     * 教师ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 部门ID
     */
    @TableField("dept_id")
    private Long deptId;

    /**
     * 是否主管, 1:是, 0:否
     */
    @TableField("is_admin")
    private Integer isAdmin;

    /**
     * 是否主部门, 1:是, 0:否
     */
    @TableField("is_master")
    private Integer isMaster;
}