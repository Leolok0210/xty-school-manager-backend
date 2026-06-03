package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class StudentAttendancePageResModel {
    @ApiModelProperty("id")
    private Long id;
    @ApiModelProperty("学生名称")
    private String studentName;
    @ApiModelProperty("学生编号")
    private String studentNo;
    @ApiModelProperty("级组名称")
    private String gradeName;
    @ApiModelProperty("班级名称")
    private String className;
    @ApiModelProperty("日期")
    private LocalDate attendanceDate;
    @ApiModelProperty("上午入校时间")
    private LocalTime morningInTime;
    @ApiModelProperty("上午离校时间")
    private LocalTime morningOutTime;
    @ApiModelProperty("下午入校时间")
    private LocalTime afternoonInTime;
    @ApiModelProperty("下午离校时间")
    private LocalTime afternoonOutTime;
    @ApiModelProperty("出勤状态（0.正常;1.迟到;2.早退;3.缺卡;4.数据异常）")
    private String status;
    @ApiModelProperty("备注")
    private String remark;
}