package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(description = "学部成绩权重规则学部返回类") // 修改描述
public class DepartmentScoreRuleDepartmentResModel {
    @ApiModelProperty(value = "级组id")
    private Long groupId;

    @ApiModelProperty(value = "学部成绩权重规则详情")
    List<DepartmentScoreRuleDetailResModel> details;

}