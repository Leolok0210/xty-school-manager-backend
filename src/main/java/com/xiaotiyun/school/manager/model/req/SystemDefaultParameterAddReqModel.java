package com.xiaotiyun.school.manager.model.req;

import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel(description = "系统预设参数新增请求对象")
public class SystemDefaultParameterAddReqModel {
    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty(value = "类型", example = "REST-大小息，PERF-课堂，APPEARANCE-仪表不符，ROUNDS-巡堂登记", required = true)
    private String typeGroup;

    @ApiModelProperty(value = "代码", example = "1")
    private String code;

    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty(value = "值", example = "未佩戴校徽", required = true)
    private String value;
}
