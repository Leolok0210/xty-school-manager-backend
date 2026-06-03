package com.xiaotiyun.school.manager.model.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@ApiModel("用户绑定学校请求")
public class UserBindSchoolReqModel {
    @NotNull(message = "用户ID不能为空")
    @ApiModelProperty(value = "用户ID", required = true)
    private Long userId;

    @NotNull(message = "学校ID不能为空")
    @ApiModelProperty(value = "学校ID", required = true)
    private Long schoolId;

    @NotBlank(message = "用户名称不能为空")
    @ApiModelProperty(value = "用户名称", required = true)
    private String username;

    @NotNull(message = "用户类型不能为空")
    @ApiModelProperty(value = "用户类型(1:教师 2:职员 3:专职人员 4:工友)", required = true)
    private Integer userType;

    @NotBlank(message = "职务不能为空")
    @ApiModelProperty(value = "职务", required = true)
    private String position;

    @NotNull(message = "性别不能为空")
    @ApiModelProperty(value = "性别(1:男 2:女)", required = true)
    private Integer gender;

    @NotNull(message = "状态不能为空")
    @ApiModelProperty(value = "状态(1:在职 2:离职)", required = true)
    private Integer status;

    @NotEmpty(message = "用户组ID列表不能为空")
    @ApiModelProperty(value = "用户组ID列表", required = true)
    private List<Long> userGroupIds;
} 