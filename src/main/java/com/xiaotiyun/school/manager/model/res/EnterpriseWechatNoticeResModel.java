package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 企业微信通知响应模型
 */
@Data
@ApiModel(value = "企业微信通知响应模型")
public class EnterpriseWechatNoticeResModel {

    @ApiModelProperty(value = "ID")
    private Long id;

    @ApiModelProperty(value = "学年")
    private String schoolYear;

    @ApiModelProperty(value = "通知类型(1-自定义通知,2-余暇活动,3-健康申报)")
    private Integer noticeType;

    @ApiModelProperty(value = "发送对象(1-全部,2-学生,3-家长)")
    private Integer targetType;

    @ApiModelProperty(value = "筛选值(JSON格式)")
    private String filterValue;

    @ApiModelProperty(value = "标题")
    private String title;

    @ApiModelProperty(value = "通知内容")
    private String noticeContent;

    @ApiModelProperty(value = "附件信息")
    private List<String> files;

    @ApiModelProperty(value = "状态(0-待发送,1-已发送)")
    private Integer status;

    @ApiModelProperty(value = "类型(1-小程序,2-H5)")
    private Integer type;

    @ApiModelProperty(value = "发送时间")
    private LocalDateTime sendTime;

    @ApiModelProperty(value = "操作人")
    private String creatorName;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "更新时间")
    private LocalDateTime updateTime;
}