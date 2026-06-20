package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("设备考勤记录响应")
public class DeviceAttendanceResModel {
    @ApiModelProperty("记录ID")
    private Long id;

    @ApiModelProperty("学生学号")
    private String studentId;

    @ApiModelProperty("学生姓名")
    private String name;

    @ApiModelProperty("打卡时间")
    private Long time;

    @ApiModelProperty("打卡状态")
    private String status;

    @ApiModelProperty("班级ID")
    private Long classId;

    @ApiModelProperty("设备序列号")
    private String deviceSn;
}
