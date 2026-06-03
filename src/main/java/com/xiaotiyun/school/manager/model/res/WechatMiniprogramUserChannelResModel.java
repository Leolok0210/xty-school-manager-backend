package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "小程序渠道用户信息")
public class WechatMiniprogramUserChannelResModel {

    /**
     * 主键
     */
    @ApiModelProperty(value = "主键")
    private Long id;

    /**
     * 渠道ID
     */
    @ApiModelProperty(value = "渠道ID")
    private Long channelId;

    /**
     * 渠道Url
     */
    @ApiModelProperty(value = "渠道Url")
    private String channelUrl;

    /**
     * 渠道名称
     */
    @ApiModelProperty(value = "渠道名称")
    private String channelName;

    /**
     * 渠道类型
     */
    @ApiModelProperty(value = "渠道公有,0-否,1-是")
    private Integer channelPublic;

    /**
     * 学生名称
     */
    @ApiModelProperty(value = "学生名称")
    private String studentName;

    /**
     * 学生ID
     */
    @ApiModelProperty(value = "学生ID")
    private Long studentId;
}
