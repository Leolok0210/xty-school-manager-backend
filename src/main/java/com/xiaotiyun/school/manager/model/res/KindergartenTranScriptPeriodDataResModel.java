package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@ApiModel(description = "成绩单学段数据")
public class KindergartenTranScriptPeriodDataResModel {
    
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

    @ApiModelProperty(value = "学生素质评分详情")
    private List<KindergartenStudentQualityScoreModel> resModels;
    //评语
    @ApiModelProperty("评语")
    private String comments;

    //平均分
    @ApiModelProperty("平均分")
    private Double averageScore;

    @ApiModelProperty("请假节数")
    private Integer leavePeriods;
    
    @ApiModelProperty("缺席节数")
    private Integer absencePeriods;
    
    @ApiModelProperty("迟到次数")
    private Integer lateTimes;
    
    @ApiModelProperty("奖项数")
    private List<String> awards;

} 