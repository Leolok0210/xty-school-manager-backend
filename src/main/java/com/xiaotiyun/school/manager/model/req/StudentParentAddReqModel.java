package com.xiaotiyun.school.manager.model.req;

import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 学生家长信息新增请求模型
 */
@Data
public class StudentParentAddReqModel {

    @ApiModelProperty(value = "家长ID，若是修改必传")
    private Long id;

    @ApiModelProperty(value = "学校ID", required = true)
    @NotNull(message = "学校ID不能为空")
    private Long schoolId;

    @ApiModelProperty(value = "学生ID", required = true)
    @NotNull(message = "学生ID不能为空")
    private Long studentId;

    @ApiModelProperty(value = "家长名称")
    private String parentName;

    @ApiModelProperty(value = "家长手机号")
    private String parentPhone;

    @ApiModelProperty(value = "家长与学生关系")
    private String parentRelation;

    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty(value = "家长类型:1-父亲关系;2-母亲关系;3-监护人关系;4-其他家长关系;")
    private String parentType;

    @ApiModelProperty(value = "是否接受短讯")
    private Boolean acceptSms;

    @ApiModelProperty(value = "职业")
    private String job;

    @ApiModelProperty(value = "任职单位")
    private String jobUnit;

    @ApiModelProperty(value = "与监护人同住")
    private Boolean withGuardian;

    @ApiModelProperty(value = "监护人流动电话")
    private String guardianMobile;

    @ApiModelProperty(value = "地址区域ID")
    private String addressAreaId;

    @ApiModelProperty(value = "监护人住址")
    private String guardianAddress;
}
