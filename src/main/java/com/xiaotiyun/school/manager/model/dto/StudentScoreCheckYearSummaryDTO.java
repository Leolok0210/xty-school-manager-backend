package com.xiaotiyun.school.manager.model.dto;

import com.xiaotiyun.school.manager.model.res.TranScriptSubjectScoreResModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(description = "成绩单学年总评")
public class StudentScoreCheckYearSummaryDTO {
    
    @ApiModelProperty("科目成绩列表")
    private List<StudentScoreCheckSubjectScoreDTO> subjectScores;
    
    @ApiModelProperty("平均分 *100")
    private Double averageScore;
    
    @ApiModelProperty("班内名次")
    private Integer rank;
    
    @ApiModelProperty("全班人数")
    private Integer totalStudents;
    
    @ApiModelProperty("操行等级")
    private String conduct;

    @ApiModelProperty("操行得分")
    private Long conductScore;

    @ApiModelProperty("操行得分展示 1-展示")
    private Integer showScore;
} 