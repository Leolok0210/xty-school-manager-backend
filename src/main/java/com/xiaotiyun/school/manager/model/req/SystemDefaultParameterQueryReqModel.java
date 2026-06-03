package com.xiaotiyun.school.manager.model.req;

import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel(description = "系统预设参数请求对象")
public class SystemDefaultParameterQueryReqModel {
    @ApiModelProperty(value = "学校ID", example = "1")
    private Long schoolId;

    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty(value = "类型", example = "REST-大小息，PERF-课堂，APPEARANCE-仪表不符，ROUNDS-巡堂登记")
    private String typeGroup;

    @ApiModelProperty(value = "代码", example = "1")
    private String code;

    @ApiModelProperty(value = "值", example = "未佩戴校徽")
    private String value;

    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty(value = "页码", example = "1")
    private Integer pageNum;

    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty(value = "每页大小", example = "10")
    private Integer pageSize;
}