package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(description = "科目评级规则响应类")
public class SubjectLevelRuleResModel {
    @ApiModelProperty(value = "成绩展示规则", example = "枚举:0-分数，1-评级")
    private Integer showRule;

    @ApiModelProperty(value = "科目关联ID")
    private Long subjectId;

    @ApiModelProperty(value = "科目编码")
    private String subjectNumber;

    @ApiModelProperty(value = "科目名称")
    private String subjectName;

    @ApiModelProperty(value = "规则列表")
    List<SubjectLevelRuleDetailResModel> detailList;

}