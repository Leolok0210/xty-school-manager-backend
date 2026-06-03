package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 活动学生管理列表响应模型
 */
@Data
@ApiModel("活动学生管理列表响应模型")
public class ActivityStudentApplyReportListResModel {

    @ApiModelProperty("学生姓名")
    private String studentName;

    @ApiModelProperty("班级名称")
    private String className;

    @ApiModelProperty("级组名称")
    private String gradeGroupName;

    @ApiModelProperty("学生ID")
    private Long studentId;

    @ApiModelProperty("学生编号")
    private String studentNo;

    @ApiModelProperty("志愿列表")
    private List<ActivityStudentApplyReportVolunteerResModel> volunteerList;
} 