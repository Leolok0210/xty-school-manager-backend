package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "学部成绩权重规则详情返回类") // 修改描述
public class DepartmentScoreRuleDetailResModel {
    @ApiModelProperty(value = "成绩类型,0-平时成绩,1-考试成绩,2-科目成绩 3-公共+文科 4-公共+理科 6-公共+商科")
    private String scoreType;

    @ApiModelProperty(value = "科目ID", example = "scoreType=2时，必传")
    private Long subjectId;

    @ApiModelProperty(value = "科目名称")
    private String subjectName;

    @ApiModelProperty(value = "科目英文名称")
    private String subjectEnglishName;

    @ApiModelProperty(value = "权重单位%，结果*100")
    private Integer weight;

}