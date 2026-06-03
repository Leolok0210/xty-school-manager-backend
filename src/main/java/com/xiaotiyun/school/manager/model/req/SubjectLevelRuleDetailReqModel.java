package com.xiaotiyun.school.manager.model.req;

import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel(description = "科目评级规则详情请求类")
public class SubjectLevelRuleDetailReqModel {
    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty(value = "规则等级", example = "A、B、C", required = true)
    private String ruleLevel;

    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty(value = "规则最大值", required = true)
    private Integer ruleMax;

    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty(value = "规则最小值", required = true)
    private Integer ruleMin;

}