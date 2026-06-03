package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(description = "成绩单学年总评")
public class KindergartenTranScriptYearSummaryResModel {
    
    @ApiModelProperty("科目成绩列表")
    private List<TranScriptSubjectScoreResModel> subjectScores;

    @ApiModelProperty(value = "学生素质评分详情")
    private List<KindergartenStudentQualityScoreModel> resModels;

    @ApiModelProperty("平均分")
    private Double averageScore;

    @ApiModelProperty("请假总节数")
    private Integer totalLeavePeriods;

    @ApiModelProperty("缺席总节数")
    private Integer totalAbsencePeriods;

    @ApiModelProperty("迟到总次数")
    private Integer totalLateTimes;
//
//    @ApiModelProperty("奖项总数")
//    private List<String> totalAwards;



} 