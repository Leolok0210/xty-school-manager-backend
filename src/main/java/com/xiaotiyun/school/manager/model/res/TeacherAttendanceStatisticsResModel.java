package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

@Data
public class TeacherAttendanceStatisticsResModel {
    @ApiModelProperty("日期")
    private LocalDate date;
    @ApiModelProperty("考勤状态 1-正常 2-迟到 3-早退 4-缺卡【可能多状态“,”隔开】")
    private String status;
    @ApiModelProperty("是否请假")
    private Boolean isLeave;
}