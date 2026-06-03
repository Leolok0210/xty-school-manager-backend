package com.xiaotiyun.school.manager.model.req;

import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel(description = "企业微信关联关系请求对象")
public class EnterpriseWechatRelCheckReqModel {

    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty(value = "关联id", example = "1", required = true)
    private Long relId;

    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty(value = "关联类型 1-级组 2-班级 3-学生 4-家长 5-学部", example = "2", required = true)
    private Integer type;

    /**
     * 关联企业微信id 没有传入关联id
     */
    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty(value = "关联企业微信id 没有传入关联id", example = "1", required = true)
    private String wxId;

    /**
     * 是否在企业微信里面存在 0-不存在 1-存在
     */
    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty(value = "是否在企业微信里面存在 0-不存在 1-存在", example = "1", required = true)
    private Integer exist;

    /**
     * 学年
     */
    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty(value = "学年", example = "2020-2021")
    private String schoolYear;


    /**
     * 父级id
     */
    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty(value = "父级id", example = "1")
    private String parentId;
    /**
     *
     */
    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty(value = "学校id", example = "学校id-2021")
    private Long schoolId;
}