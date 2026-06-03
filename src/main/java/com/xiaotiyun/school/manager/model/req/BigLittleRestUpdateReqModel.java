package com.xiaotiyun.school.manager.model.req;

import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel(description = "大息小息表現登記请求对象")
public class BigLittleRestUpdateReqModel {
    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty(value = "主键ID", example = "1", required = true)
    private Long id;

    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty(value = "日期", example = "2023-10-01")
    private String registrationDate;

    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty(value = "类型", example = "大息/小息")
    private String type;

    @ApiModelProperty(value = "大息小息表現ID", example = "1")
    private Long registrationId;

    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty(value = "大息小息表現", example = "未佩戴校徽")
    private String registrationContent;
}