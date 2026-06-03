package com.xiaotiyun.school.manager.model.req;

import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel("删除用户请求")
public class UserDeleteReqModel {

    @ApiModelProperty("学校ID")
    private Long schoolId;

    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty("用户ID")
    private Long id;

    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty("是否预删除，0-否，1-是")
    private Integer isPre;

    @ApiModelProperty("是否全删除，0-否，1-是, 当不是预删除时，必填")
    private Integer isAll;

    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty("当前部门ID，根目录传0")
    private Long deptId;
}