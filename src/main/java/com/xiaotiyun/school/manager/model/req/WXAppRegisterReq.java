package com.xiaotiyun.school.manager.model.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "微信应用注册校验请求类")
public class WXAppRegisterReq {
    @ApiModelProperty(value = "加密")
    private String msg_signature;

    @ApiModelProperty(value = "时间戳")
    private String timestamp;

    @ApiModelProperty(value = "随机数", example = "1")
    private String nonce;

    @ApiModelProperty(value = "随机字符串", example = "1")
    private String echostr;
}