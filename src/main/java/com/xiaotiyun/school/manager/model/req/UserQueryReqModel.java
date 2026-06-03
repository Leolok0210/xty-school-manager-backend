package com.xiaotiyun.school.manager.model.req;

import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel("用户查询请求")
public class UserQueryReqModel {

    @ApiModelProperty("用户ID")
    private Long id;

//    @ApiModelProperty("用户名")
//    private String loginName;
//
//    @ApiModelProperty("用户名称")
//    private String username;
//
//    @ApiModelProperty("用户编号")
//    private String userNumber;
//
//    @ApiModelProperty("手机号")
//    private String phone;

    @ApiModelProperty("查询文本，聚合查询用户名、用户名称、用户编号、手机号")
    private String searchText;

    @ApiModelProperty("用户组ID")
    private Long groupId;

//    @ApiModelProperty("用户类型(1:教师 2:职员 3:专职人员 4:工友)")
//    private Integer userType;

//    @ApiModelProperty("职务")
//    private String position;

    @ApiModelProperty("性别(1:男 2:女)")
    private Integer gender;

    @ApiModelProperty("部门ID，根目录不传或传0")
    private Long deptId;

    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty("页码")
    private Integer pageNum = 1;

    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty("每页条数")
    private Integer pageSize = 10;
} 