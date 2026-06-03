package com.xiaotiyun.school.manager.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaotiyun.school.manager.basic.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user_school_rel")
public class UserSchoolRelEntity extends BaseEntity {
    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 学校ID
     */
    private Long schoolId;
    
    /**
     * 用户名称
     */
    private String username;

    /**
     * 用户编号
     */
    private String userNumber;

    /**
     * 用户类型(1:教师 2:职员 3:专职人员 4:工友)
     */
    private Integer userType;
    
    /**
     * 职务
     */
    private String position;
    
    /**
     * 性别(1:男 2:女)
     */
    private Integer gender;
    
    /**
     * 状态(1:在职 2:离职)
     */
    private Integer status;
    
    /**
     * 用户组ID列表,多个用逗号分隔
     */
    private String userGroupIds;
} 