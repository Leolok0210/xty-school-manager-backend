package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(description = "成绩单学年总评")
public class TranScriptYearSummaryResModel {
    
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
    
    @ApiModelProperty("请假总节数")
    private Integer totalLeavePeriods;
    
    @ApiModelProperty("缺席总节数")
    private Integer totalAbsencePeriods;
    
    @ApiModelProperty("迟到总次数")
    private Integer totalLateTimes;
    
    @ApiModelProperty("奖项总数")
    private Integer totalAwards;
    
    @ApiModelProperty("优点总数")
    private Integer totalMerits;
    
    @ApiModelProperty("小功总数")
    private Integer totalGoodServices;
    
    @ApiModelProperty("大功总数")
    private Integer totalOutstandingServices;
    
    @ApiModelProperty("缺点总数")
    private Integer totalDemerits;
    
    @ApiModelProperty("小过总数")
    private Integer totalMinorFaults;
    
    @ApiModelProperty("大过总数")
    private Integer totalMajorFaults;

    //totalVolunteerHours
    @ApiModelProperty("总志愿时数")
    private Integer totalVolunteerHours;
} 