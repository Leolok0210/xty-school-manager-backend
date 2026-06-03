package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "小程序渠道响应对象")
public class WechatMiniprogramChannelResModel {

    @ApiModelProperty(value = "渠道id")
    private Long id;

    @ApiModelProperty(value = "渠道名称")
    private String channelName;

    @ApiModelProperty(value = "渠道域名地址url")
    private String channelUrl;
}
