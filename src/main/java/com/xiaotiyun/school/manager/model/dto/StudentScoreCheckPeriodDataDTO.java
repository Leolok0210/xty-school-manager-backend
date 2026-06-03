package com.xiaotiyun.school.manager.model.dto;

import com.xiaotiyun.school.manager.model.res.TranScriptSubjectScoreResModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@ApiModel(description = "成绩单学段数据")
public class StudentScoreCheckPeriodDataDTO {
    
    @ApiModelProperty("学段ID")
    private Long periodId;
    
    @ApiModelProperty("学段名称")
    private String periodName;
    
    @ApiModelProperty("学段占比")
    private Integer proportion;

    //学段时间
    @ApiModelProperty("学段开始时间")
    private LocalDateTime startTime;
    
    @ApiModelProperty("科目成绩列表")
    private List<StudentScoreCheckSubjectScoreDTO> subjectScores;
    
    @ApiModelProperty("平均分")
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