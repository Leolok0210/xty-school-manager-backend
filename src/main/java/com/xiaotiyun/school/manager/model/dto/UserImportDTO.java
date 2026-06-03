package com.xiaotiyun.school.manager.model.dto;

import lombok.Data;

@Data
public class UserImportDTO {
    /**
     * id
     */
    private Long id;
    /**
     * 学校id
     */
    private Long schoolId;
    /**
     * 用户名称
     */
    private String username;
    /**
     * 手机号
     */
    private String mobile;
    /**
     * 密码
     */
    private String password;
    /**
     * 登录用户名
     */
    private String loginName;
    /**
     * 性别(1:男,2:女)
     */
    private Integer gender;
    /**
     * 手机号区号
     */
    private String mobileHead;
    /**
     * 用户组
     */
    private String userGroup;
    /**
     * 用户编号
     */
    private String userNumber;
    /**
     * 所属部门
     */
    private String deptName;
    /**
     * 用户职务
     */
//    private String position;
}
