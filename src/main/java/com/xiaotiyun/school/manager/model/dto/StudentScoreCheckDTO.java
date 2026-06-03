package com.xiaotiyun.school.manager.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@ApiModel(description = "成绩单生成响应参数")
public class StudentScoreCheckDTO {
    //学校id
    @ApiModelProperty("学校ID")
    private Long schoolId;
    //班级id
    @ApiModelProperty("班级ID")
    private Long classId;

    //学生id
    @ApiModelProperty("学生ID")
    private Long studentId;

    @ApiModelProperty("artScience 1 文科 2理科")
    private Integer artScience;

    @ApiModelProperty("学生照片")
    private String studentPhoto;

    @ApiModelProperty("班级名称")
    private String className;

    @ApiModelProperty("学生姓名")
    private String studentName;

    @ApiModelProperty("班内号")
    private String studentNo;

    @ApiModelProperty("学生编号")
    private String educationNo;

    @ApiModelProperty("学年")
    private String schoolYear;

    @ApiModelProperty("学段数据列表")
    private List<StudentScoreCheckPeriodDataDTO> periodDataList;
    
    @ApiModelProperty("学年总评")
    private StudentScoreCheckYearSummaryDTO yearSummary;
} 