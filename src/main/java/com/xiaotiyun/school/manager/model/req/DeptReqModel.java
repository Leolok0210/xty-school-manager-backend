package com.xiaotiyun.school.manager.model.req;

import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 部门请求参数类
 */
@Data
@ApiModel(description = "部门请求参数")
public class DeptReqModel {
    /**
     * 主键ID
     */
    @ApiModelProperty(value = "主键ID，修改时必传")
    private Long id;

    /**
     * 学校ID
     */
    @ApiModelProperty(value = "学校ID")
    private Long schoolId;

    /**
     * 部门名称
     */
    @Size(max = 20, message = LanguageConstants.NAME_MAX_LENGTH)
    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty(value = "部门名称", required = true)
    private String name;

    /**
     * 父部门ID
     */
    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty(value = "父部门ID", required = true)
    private Long parentId;

    /**
     * 主管ID
     */
    @ApiModelProperty(value = "主管ID,教师ID")
    private Long managerId;
}