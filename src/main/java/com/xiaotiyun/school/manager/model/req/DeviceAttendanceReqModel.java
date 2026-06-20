package com.xiaotiyun.school.manager.model.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("设备考勤记录请求")
public class DeviceAttendanceReqModel {
    @ApiModelProperty("学生学号")
    private String studentId;

    @ApiModelProperty("学生姓名")
    private String name;

    @ApiModelProperty("打卡时间 (毫秒时间戳)")
    private Long time;

    @ApiModelProperty("打卡状态")
    private String status;

    @ApiModelProperty("班级ID")
    private Long classId;

    @ApiModelProperty("设备序列号")
    private String deviceSn;
}
