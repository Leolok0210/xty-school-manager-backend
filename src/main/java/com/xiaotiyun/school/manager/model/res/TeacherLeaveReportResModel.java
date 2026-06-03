package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("教师出勤报表响应参数")
public class TeacherLeaveReportResModel {
    @ApiModelProperty("教师id")
    private Long teacherId;

    @ApiModelProperty("教师名称")
    private String teacherName;

    @ApiModelProperty("职务")
    private String teacherPosition;

    @ApiModelProperty("编号")
    private String teacherNumber;

    @ApiModelProperty("应出勤天数")
    private Integer shouldAttendanceDays;

    @ApiModelProperty("实际出勤天数")
    private Integer actualAttendanceDays;

    @ApiModelProperty("迟到次数")
    private Integer lateCount;

    @ApiModelProperty("早退次数")
    private Integer earlyCount;

    @ApiModelProperty("请假次数")
    private Integer leaveCount;

    @ApiModelProperty("公务次数")
    private Integer officialCount;

    @ApiModelProperty("无由不打卡次数")
    private Integer noReasonCount;
}