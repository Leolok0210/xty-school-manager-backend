package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class StudentExamScoreResModel {
    @ApiModelProperty("考试时间")
    private LocalDateTime examTime;
    @ApiModelProperty("科目ID")
    private Long subjectId;
    @ApiModelProperty("科目名称")
    private String subjectName;
    @ApiModelProperty("课程类型，1-选修 2-必修")
    private String subjectType;
    @ApiModelProperty("成绩*100")
    private Integer score;
}