package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class MinigrogramAuthResModel implements Serializable {

    @ApiModelProperty(value = "用户code，用于渠道绑定或本服务用户绑定")
    private String userCode;

    @ApiModelProperty(value = "是否绑定渠道")
    private boolean channelBind;

    @ApiModelProperty(value = "渠道类型，1-公共渠道，0-私有渠道")
    private Integer channelType;

    @ApiModelProperty(value = "绑定渠道域名url")
    private String url;

    @ApiModelProperty(value = "是否绑定本服务用户")
    private boolean serviceBind;

    @ApiModelProperty(value = "本服务token，若渠道为本服务，则返回token")
    private String token;

    @ApiModelProperty(value = "渠道ID，绑定为公有时，才会返回")
    private Long channelUserId;

    @ApiModelProperty(value = "用户信息")
    private MinigrogramUserDetailResModel user;

    @ApiModelProperty(value = "渠道用户信息")
    private List<WechatMiniprogramUserChannelResModel> channelUser;
}
