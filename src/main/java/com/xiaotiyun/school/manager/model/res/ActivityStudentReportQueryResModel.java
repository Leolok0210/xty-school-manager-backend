package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 活动学生选课情况查询响应模型
 */
@Data
@ApiModel("活动学生选课情况查询响应模型")
public class ActivityStudentReportQueryResModel {

    @ApiModelProperty("学生姓名")
    private String studentName;

    @ApiModelProperty("学生ID")
    private Long studentId;

    @ApiModelProperty("班级名称")
    private String className;

    @ApiModelProperty("级组名称")
    private String gradeGroupName;

    @ApiModelProperty("学生编号")
    private String studentNo;

} 