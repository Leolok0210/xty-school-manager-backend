package com.xiaotiyun.school.manager.model.req;

import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel(value = "系统预设参数修改对象")
public class SystemDefaultParameterUpdateReqModel {
    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty(value = "主键ID", example = "1", required = true)
    private Long id;

    @ApiModelProperty(value = "代码", example = "1")
    private String code;

    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty(value = "值", example = "未佩戴校徽", required = true)
    private String value;
}
