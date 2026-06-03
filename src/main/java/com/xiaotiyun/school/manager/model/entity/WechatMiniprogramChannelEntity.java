package com.xiaotiyun.school.manager.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaotiyun.school.manager.basic.common.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 小程序渠道表实体类
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("wechat_miniprogram_channel")
public class WechatMiniprogramChannelEntity extends BaseEntity {

    @ApiModelProperty(value = "渠道名称")
    private String channelName;

    @ApiModelProperty(value = "渠道域名地址url")
    private String channelUrl;

    @ApiModelProperty(value = "渠道公有,0-否,1-是")
    private Integer channelPublic;
}
