package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@ApiModel(description = "成绩单学段数据")
public class TranScriptPeriodDataResModel {
    
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
    private List<TranScriptSubjectScoreResModel> subjectScores;
    
    @ApiModelProperty("平均分")
    private Double averageScore;
    
    @ApiModelProperty("班内名次")
    private Integer rank;
    
    @ApiModelProperty("全班人数")
    private Integer totalStudents;
    
    @ApiModelProperty("操行等级")
    private String conduct;
    
    @ApiModelProperty("请假节数")
    private Integer leavePeriods;
    
    @ApiModelProperty("缺席节数")
    private Integer absencePeriods;
    
    @ApiModelProperty("迟到次数")
    private Integer lateTimes;
    
    @ApiModelProperty("奖项数")
    private Integer awards;
    
    @ApiModelProperty("优点数")
    private Integer merits;
    
    @ApiModelProperty("小功数")
    private Integer goodServices;
    
    @ApiModelProperty("大功数")
    private Integer outstandingServices;
    
    @ApiModelProperty("缺点数")
    private Integer demerits;
    
    @ApiModelProperty("小过数")
    private Integer minorFaults;
    
    @ApiModelProperty("大过数")
    private Integer majorFaults;

    //义工时数
    @ApiModelProperty("义工时数")
    private Integer volunteerHours;
} 