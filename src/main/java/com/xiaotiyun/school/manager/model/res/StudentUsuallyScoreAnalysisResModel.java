package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class StudentUsuallyScoreAnalysisResModel {
    @ApiModelProperty("学生id")
    private Long studentId;
    @ApiModelProperty("成绩*100")
    private Integer score;
    @ApiModelProperty("学生姓名")
    private String studentName;
}