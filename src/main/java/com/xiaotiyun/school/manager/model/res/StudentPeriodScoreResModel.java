package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(description = "学生学段成绩信息")
public class StudentPeriodScoreResModel {
    
    @ApiModelProperty("学生ID")
    private Long studentId;
    
    @ApiModelProperty("学段ID")
    private Long periodId;
    
    @ApiModelProperty("科目成绩列表")
    private List<StudentSubjectScoreResModel> subjectScores;
} 