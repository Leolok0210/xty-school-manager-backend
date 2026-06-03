package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "科目评级规则详情返回类")
public class SubjectLevelRuleDetailResModel {
    @ApiModelProperty(value = "规则等级", example = "A、B、C", required = true)
    private String ruleLevel;

    @ApiModelProperty(value = "规则最大值", required = true)
    private Integer ruleMax;

    @ApiModelProperty(value = "规则最小值", required = true)
    private Integer ruleMin;

}