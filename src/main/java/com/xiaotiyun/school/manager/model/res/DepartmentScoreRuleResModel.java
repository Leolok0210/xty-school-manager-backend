package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(description = "学部成绩权重规则响应类") // 修改描述
public class DepartmentScoreRuleResModel {

    @ApiModelProperty(value = "学部(0-直接平均 1-加权平均)")
    private Integer avgType;

    @ApiModelProperty(value = "学部成绩权重规则学部详情")
    List<DepartmentScoreRuleDepartmentResModel> details;

}