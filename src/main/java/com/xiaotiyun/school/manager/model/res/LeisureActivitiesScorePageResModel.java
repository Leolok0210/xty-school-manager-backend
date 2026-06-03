package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("余暇活动成绩信息")
public class LeisureActivitiesScorePageResModel {
    @ApiModelProperty("ID")
    private Long id;
    @ApiModelProperty("课程id")
    private Long courseId;
    @ApiModelProperty("课程名称")
    private String courseName;
    @ApiModelProperty("学生id")
    private Long studentId;
    @ApiModelProperty("学生名称")
    private String studentName;
    @ApiModelProperty("级组名称")
    private String groupName;
    @ApiModelProperty("班级名称")
    private String className;
    @ApiModelProperty("班内号")
    private Integer seatNo;
    @ApiModelProperty("学生编号")
    private String studentNo;
    @ApiModelProperty("出席次数")
    private Integer attendCount;
    @ApiModelProperty("出席率分数(*100)")
    private Integer attendScore;
    @ApiModelProperty("课节表现分数(*100)")
    private Integer lessonScore;
    @ApiModelProperty("总分数(*100)")
    private Integer totalScore;
    @ApiModelProperty("参考成绩等级")
    private String referenceLevel;
    @ApiModelProperty("最终成绩等级")
    private String finalLevel;
}