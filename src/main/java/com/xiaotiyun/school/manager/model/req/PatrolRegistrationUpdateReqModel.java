package com.xiaotiyun.school.manager.model.req;

import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel(description = "巡堂登记新增对象")
public class PatrolRegistrationUpdateReqModel {
    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty(value = "主键ID", example = "1")
    private Long id;

    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty(value = "日期", example = "2023-10-01")
    private String registrationDate;

//    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty(value = "课节ID", example = "1")
    private Long lessonPeriodId;

    @ApiModelProperty(value = "巡堂记录内容ID", example = "1")
    private Long registrationId;

    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty(value = "登记内容", example = "未佩戴校徽")
    private String registrationContent;
}