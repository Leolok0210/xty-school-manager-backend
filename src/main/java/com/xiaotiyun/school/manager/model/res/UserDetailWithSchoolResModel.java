package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("用户详情响应")
public class UserDetailWithSchoolResModel {
    @ApiModelProperty("用户ID")
    private Long id;

    @ApiModelProperty("用户名称")
    private String username;

    @ApiModelProperty("手机号")
    private String mobile;

    @ApiModelProperty("用户组ID列表")
    private List<Long> userGroupIds;

    @ApiModelProperty("用户类型(1:教师 2:职员 3:专职人员 4:工友)")
    private Integer userType;

    @ApiModelProperty("职务")
    private String position;

    @ApiModelProperty("性别(1:男 2:女)")
    private Integer gender;

    @ApiModelProperty("状态(1:在职 2:离职)")
    private Integer status;

    @ApiModelProperty("是否需要重置密码(0:不需要 1:需要)")
    private Integer needResetPwd;

    @ApiModelProperty("学校ID列表")
    private List<Long> schoolIds;
} 