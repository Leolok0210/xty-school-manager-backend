package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 二次报名管理列表响应模型
 */
@Data
@ApiModel("二次报名管理列表响应模型")
public class ActivityStudentApplyReportSecondListResModel {

    @ApiModelProperty("学生姓名")
    private String studentName;

    @ApiModelProperty("学生ID")
    private Long studentId;

    @ApiModelProperty("学生编号")
    private String studentNo;

    @ApiModelProperty("课程名称")
    private String lensonName;

    @ApiModelProperty("课程id")
    private String lensonId;

    @ApiModelProperty("班级名称")
    private String className;

    @ApiModelProperty("级组名称")
    private String gradeGroupName;
} 