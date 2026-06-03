package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 当前活动响应模型
 */
@Data
@ApiModel("当前活动响应模型")
public class ActivityStudentApplyReportCurrentActivityResModel {

    @ApiModelProperty("活动名称")
    private String activityName;

    @ApiModelProperty("活动ID")
    private Long activityId;

    @ApiModelProperty("活动学年")
    private String activityYear;

    @ApiModelProperty("活动学部")
    private String activityDepartment;

    @ApiModelProperty("活动学段")
    private String activityStage;

    @ApiModelProperty("是否是当前活动")
    private boolean currentActivity;

    @ApiModelProperty("志愿列表")
    private List<ActivityStudentApplyReportVolunteerResModel> volunteerList;

    @ApiModelProperty("状态（-1 预先导入 1-待公布 2已匹配，3匹配失败）")
    private Integer status;

    @ApiModelProperty("1 一次报名 2-二次报名")
    private Integer type;

    @ApiModelProperty("课程列表")
    private List<ActivityStudentApplyReportMyCourseResModel> courseList;
} 