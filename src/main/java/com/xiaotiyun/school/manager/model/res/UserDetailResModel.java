package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("用户详情响应")
public class UserDetailResModel {
    @ApiModelProperty("用户ID")
    private Long id;

    @ApiModelProperty("手机号")
    private String mobile;

    @ApiModelProperty(value = "手机号区号")
    private String mobileHead;

    @ApiModelProperty("用户编号")
    private String userNumber;

    @ApiModelProperty("用户名")
    private String uName;

    @ApiModelProperty("用户姓名")
    private String username;

    @ApiModelProperty("用户名")
    private String loginName;

    @ApiModelProperty("用户类型 1-普通用户 2-超级管理员 3-学校管理员")
    private Integer userType;

    @ApiModelProperty("学校的用户类型(1:教师 2:职员 3:专职人员 4:工友)")
    private Integer schoolUserType;

//    @ApiModelProperty("职务")
//    private String position;

    @ApiModelProperty("性别(1:男 2:女)")
    private Integer gender;

//    @ApiModelProperty("状态(1:在职 2:离职)")
//    private Integer status;

    @ApiModelProperty("用户组ID列表")
    private String userGroupIds;

    @ApiModelProperty("用户组名称")
    private String userGroupName;

    @ApiModelProperty("是否需要重置密码(0:不需要 1:需要)")
    private Integer needResetPwd;

    @ApiModelProperty("是否主部门(0:否 1:是)")
    private Integer isMasterDept;

    @ApiModelProperty("是否部门主管(0:否 1:是)")
    private Integer isDeptLeader;

    @ApiModelProperty("部门ID")
    private Long deptId;

    @ApiModelProperty("主部门名称")
    private String deptName;

    @ApiModelProperty(value = "所属部门")
    private UserDeptDetailResModel masterDept;

    @ApiModelProperty(value = "兼职部门ID列表")
    private List<UserDeptDetailResModel> slaveDeptList;

    @ApiModelProperty(value = "班级权限")
    private List<UserClassRelResModel> relList;

    @ApiModelProperty(value = "用户设置")
    private List<UserSettingResModel> userSettings;
} 