package com.xiaotiyun.school.manager.model.req;

import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel(description = "学生家长请求模型")
public class StudentParentReqModel {

    /**
     * 学校ID
     */
    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty(value = "学校ID")
    private Long schoolId;

    /**
     * 学生ID
     */
    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty(value = "学生ID")
    private Long studentId;

    /**
     * 家长名称
     */
    @ApiModelProperty(value = "家长名称")
    private String parentName;

    /**
     * 家长手机号
     */
    @ApiModelProperty(value = "家长手机号")
    private String parentPhone;

    /**
     * 家长与学生关系
     */
    @ApiModelProperty(value = "家长与学生关系")
    private String parentRelation;

    /**
     * 家长类型:1-父母关系;2-监护人关系;3-其他家长关系;
     */
    @ApiModelProperty(value = "家长类型:1-父母关系;2-监护人关系;3-其他家长关系;")
    private String parentType;

    /**
     * 是否接受短讯
     */
    @ApiModelProperty(value = "是否接受短讯")
    private Boolean acceptSms;

    /**
     * 职业
     */
    @ApiModelProperty(value = "职业")
    private String job;

    /**
     * 任职单位
     */
    @ApiModelProperty(value = "任职单位")
    private String jobUnit;

    /**
     * 与监护人同住
     */
    @ApiModelProperty(value = "与监护人同住")
    private Boolean withGuardian;

    /**
     * 监护人流动电话
     */
    @ApiModelProperty(value = "监护人流动电话")
    private String guardianMobile;

    /**
     * 地址区域ID
     */
    @ApiModelProperty(value = "地址区域ID")
    private Long addressAreaId;

    /**
     * 监护人住址
     */
    @ApiModelProperty(value = "监护人住址")
    private String guardianAddress;
}
