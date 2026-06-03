package com.xiaotiyun.school.manager.model.req;

import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * 企业微信通知更新请求模型
 */
@Data
@ApiModel(value = "企业微信通知更新请求模型")
public class EnterpriseWechatNoticeUpdateReqModel {
    @NotBlank(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty(value = "学年", required = true)
    private String schoolYear;

    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty(value = "通知类型(1-自定义通知,2-余暇活动,3-健康申报)", required = true)
    private Integer noticeType;

    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty(value = "发送对象(1-全部,2-学生,3-家长)", required = true)
    private Integer targetType;

    @NotBlank(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty(value = "筛选值(JSON格式)", required = true)
    private String filterValue;

    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @Size(min = 1, max = 20, message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty(value = "标题", required = true)
    private String title;

    @Size(min = 1, max = 150, message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty(value = "通知内容")
    private String noticeContent;

    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty(value = "类型(1-小程序,2-H5)", required = true)
    private Integer type;

    @ApiModelProperty(value = "业务ID(类型为余暇活动时必传)")
    private Long businessId;

    @ApiModelProperty(value = "文件ID")
    private List<Long> fileIds;

    @ApiModelProperty(value = "发送时间")
    private String sendTime;
}