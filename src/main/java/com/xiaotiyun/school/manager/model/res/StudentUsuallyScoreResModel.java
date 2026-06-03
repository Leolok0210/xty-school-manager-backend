package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@ApiModel("学生平时成绩信息")
@Data
public class StudentUsuallyScoreResModel {
    @ApiModelProperty("id")
    private Long id;
    @ApiModelProperty("学生id")
    private Long studentId;
    @ApiModelProperty("测验时间")
    private LocalDateTime testTime;
    @ApiModelProperty("科目ID")
    private Long subjectId;
    @ApiModelProperty("科目名称")
    private String subjectName;
    @ApiModelProperty("课程类型，1-选修 2-必修")
    private String subjectType;
    @ApiModelProperty("测验类型ID")
    private Long testType;
    @ApiModelProperty("成绩*100")
    private Integer score;
    @ApiModelProperty("测验类型名称")
    private String testTypeName;
}