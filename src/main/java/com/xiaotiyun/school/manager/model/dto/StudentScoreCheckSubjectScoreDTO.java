package com.xiaotiyun.school.manager.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "成绩单科目成绩信息")
public class StudentScoreCheckSubjectScoreDTO {
    
    @ApiModelProperty("科目id")
    private Long subjectId;

    @ApiModelProperty("科目名称")
    private String subjectName;
    
    @ApiModelProperty("成绩")
    private Integer score;

    @ApiModelProperty("成绩评级")
    private String scoreLevel;

    private Integer countedInAverage;

    @ApiModelProperty("是否 artsScience")
    private Integer artsScience;

    @ApiModelProperty(value = "成绩展示规则", example = "枚举:0-分数，1-评级")
    private Integer showRule;

    @ApiModelProperty("平时成绩")
    private Integer usuallyScore;
    @ApiModelProperty("考试成绩")
    private Integer examScore;
} 