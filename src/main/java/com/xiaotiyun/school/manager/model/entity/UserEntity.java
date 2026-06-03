package com.xiaotiyun.school.manager.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaotiyun.school.manager.basic.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user")
public class UserEntity extends BaseEntity {
    /**
     * 用户姓名
     */
    private String username;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 手机号区号
     */
    private String mobileHead;

    /**
     * 密码
     */
    private String password;

    /**
     * 用户名
     */
    private String loginName;

    /**
     * 是否需要重置密码 0-不需要 1-需要
     */
    private Integer needResetPwd;

    /**
     * 用户类型 1-普通用户 2-超级管理员
     */
    private Integer userType;


    /**
     * 性别(1:男 2:女)
     */
    private Integer gender;

    /**
     * 状态 0-禁用 1-启用
     */
    private Integer status;
} 