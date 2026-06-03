package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class StudentAttendanceReportResModel {
    @ApiModelProperty("班内号")
    private Integer seatNo;
    @ApiModelProperty("学生姓名")
    private String studentName;
    @ApiModelProperty("应到校打卡天数")
    private int clockDays;
    @ApiModelProperty("实际到校打卡天数")
    private int actualClockDays;
    @ApiModelProperty("迟到次数")
    private int beLateDays;
    @ApiModelProperty("早退次数")
    private int earlyDays;
    @ApiModelProperty("请假次数")
    private int leaveDays;
    @ApiModelProperty("公务天数")
    private int businessDays;
    @ApiModelProperty("无由不打卡天数")
    private int notClockDays;
}