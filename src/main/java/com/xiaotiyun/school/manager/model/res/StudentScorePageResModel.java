package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

@Data
public class StudentScorePageResModel {
    @ApiModelProperty("ID")
    private Long id;
    @ApiModelProperty("学年")
    private String schoolYear;
    @ApiModelProperty("学段名称")
    private String periodName;
    @ApiModelProperty("成绩类型(1.平时成绩；2.考试成绩；3.毕业成绩)")
    private Integer type;
    @ApiModelProperty("科目名称")
    private String subjectName;
    @ApiModelProperty("成绩*100")
    private Integer score;
    @ApiModelProperty("测验时间")
    private LocalDate testDate;
}