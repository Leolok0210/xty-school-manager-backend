package com.xiaotiyun.school.manager.model.req;

import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel(description = "渠道绑定请求类")
public class ChannelBindReq {

    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty(value = "渠道ID")
    private Long channelId;

    @ApiModelProperty(value = "渠道查询返回userCode")
    private String userCode;

    @ApiModelProperty(value = "wx.login返回的用户微信code")
    private String code;

    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty(value = "学生名称，该渠道下")
    private String studentName;

    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty(value = "学生编号，该渠道下")
    private Long studentId;
}
