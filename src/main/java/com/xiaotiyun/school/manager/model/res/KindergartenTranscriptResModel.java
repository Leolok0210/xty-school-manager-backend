package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@ApiModel(description = "幼稚园成绩单生成响应参数")
public class KindergartenTranscriptResModel {
    //学校id
    @ApiModelProperty("学校ID")
    private Long schoolId;
    //班级id
    @ApiModelProperty("班级ID")
    private Long classId;

    //学生id
    @ApiModelProperty("学生ID")
    private Long studentId;

    @ApiModelProperty("学校LOGO")
    private String schoolLogo;

    @ApiModelProperty("artScience 1 文科 2理科")
    private Integer artScience;

    @ApiModelProperty("学校名称")
    private String schoolName;

    @ApiModelProperty("学生照片")
    private String studentPhoto;

    @ApiModelProperty("班级名称")
    private String className;

    @ApiModelProperty("学生姓名")
    private String studentName;

    @ApiModelProperty("班内号")
    private String studentNo;

    //学生编号
    @ApiModelProperty("学生编号")
    private String educationNo;

    @ApiModelProperty("学年")
    private String schoolYear;

    @ApiModelProperty("发出日期")
    private Date issueDate;

    @ApiModelProperty("学段数据列表")
    private List<KindergartenTranScriptPeriodDataResModel> periodDataList;

    @ApiModelProperty("学年总评")
    private KindergartenTranScriptYearSummaryResModel yearSummary;


    @ApiModelProperty("备注")
    private String remarks;
} 