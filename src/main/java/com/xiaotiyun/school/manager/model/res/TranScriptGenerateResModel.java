package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@ApiModel(description = "成绩单生成响应参数")
public class TranScriptGenerateResModel {
    //学校id
    @ApiModelProperty("学校ID")
    private Long schoolId;
    //班级id
    @ApiModelProperty("班级ID")
    private Long classId;

    //学生id
    @ApiModelProperty("学生ID")
    private Long studentId;

    @ApiModelProperty("是否 artsScience")
    private Integer artScience;

    @ApiModelProperty("学校LOGO")
    private String schoolLogo;

    @ApiModelProperty("学校名称")
    private String schoolName;

    @ApiModelProperty("学生照片")
    private String studentPhoto;

    @ApiModelProperty("班级名称")
    private String className;

    @ApiModelProperty("班级编号")
    private String classNo;

    @ApiModelProperty("学生姓名")
    private String studentName;

    @ApiModelProperty("英文姓名")
    private String englishName;

    @ApiModelProperty("班内号")
    private String studentNo;

    @ApiModelProperty("学生编号")
    private String educationNo;

    @ApiModelProperty("学生证编号")
    private String studentEducationNo;

    @ApiModelProperty("学年")
    private String schoolYear;

    @ApiModelProperty("发出日期")
    private Date issueDate;

    @ApiModelProperty("学段数据列表")
    private List<TranScriptPeriodDataResModel> periodDataList;
    
    @ApiModelProperty("学年总评")
    private TranScriptYearSummaryResModel yearSummary;

    @ApiModelProperty("校外比赛记录")
    private List<TranScriptExternalCompetitionResModel> externalCompetitionRecords;

    @ApiModelProperty("备注")
    private String remarks;
} 