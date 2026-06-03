package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

@Data
public class StudentUsuallyScoreCheckDetailResModel {
    @ApiModelProperty("科目名称(按班级查看时返回)")
    private String subjectName;
    @ApiModelProperty("成绩*100")
    private Integer score;
    @ApiModelProperty("测试名称(按科目查看时返回)")
    private String taskName;
    @ApiModelProperty("测试时间(按科目查看时返回)")
    private LocalDate testDate;
}