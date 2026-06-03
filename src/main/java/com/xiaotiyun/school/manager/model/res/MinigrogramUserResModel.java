package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@ApiModel(description = "小程序登入返回对象")
@Data
public class MinigrogramUserResModel implements Serializable {

    @ApiModelProperty(value = "code")
    private String code;
    @ApiModelProperty(value = "是否绑定")
    private boolean bind;
    @ApiModelProperty(value = "token")
    private String token;
    @ApiModelProperty(value = "用户信息")
    private MinigrogramUserDetailResModel user;

    private static final long serialVersionUID = 1L;
}
