package com.xiaotiyun.school.manager.model.req;

import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel(description = "教师端微信绑定登录请求类")
public class WXBindAndLoginReq {
    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty(value = "跳转返回的code", required = true)
    private String code;

    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty(value = "用户名或手机号", required = true)
    private String loginName;

    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty(value = "密码", example = "1")
    private String password;
}