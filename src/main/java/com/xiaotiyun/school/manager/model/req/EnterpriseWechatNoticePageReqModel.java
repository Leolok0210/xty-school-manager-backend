package com.xiaotiyun.school.manager.model.req;

import com.xiaotiyun.school.manager.basic.common.PageReqModel;
import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

/**
 * 企业微信通知分页查询请求模型
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "企业微信通知分页查询请求模型")
public class EnterpriseWechatNoticePageReqModel extends PageReqModel {
    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty(value = "通知类型(1-自定义通知,2-余暇活动,3-健康申报)", required = true)
    private Integer noticeType;
}