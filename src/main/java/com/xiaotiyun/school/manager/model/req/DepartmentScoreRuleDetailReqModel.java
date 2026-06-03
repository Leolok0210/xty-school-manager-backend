package com.xiaotiyun.school.manager.model.req;

import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel(description = "学部成绩权重规则详情请求类") // 修改描述
public class DepartmentScoreRuleDetailReqModel {
    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty(value = "成绩类型,0-平时成绩,1-考试成绩,2-科目成绩 3-公共+文科/公共+理工科 4-公共+理科 6-公共+商科", required = true)
    private String scoreType;

    @ApiModelProperty(value = "科目ID", example = "scoreType=2/3/4/6时，必传", required = true)
    private Long subjectId;

    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty(value = "权重单位%，结果*100", required = true)
    private Integer weight;

}