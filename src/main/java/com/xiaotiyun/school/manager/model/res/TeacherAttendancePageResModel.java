package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@ApiModel("教师出勤响应列表")
public class TeacherAttendancePageResModel {
    @ApiModelProperty("ID")
    private Long id;
    @ApiModelProperty(value = "学校ID", example = "1001")
    private Long schoolId;
    @ApiModelProperty(value = "教师ID", example = "1001")
    private Long teacherId;
    @ApiModelProperty(value = "教师名称", example = "1001")
    private String teacherName;
    @ApiModelProperty(value = "教师编号", example = "1001")
    private String teacherNumber;
    @ApiModelProperty(value = "卡号", example = "T2023001")
    private String cardNumber;
    @ApiModelProperty(value = "考勤日期", example = "2023-10-01")
    private LocalDate attendanceDate;
    @ApiModelProperty(value = "上班时间", example = "08:30:00")
    private LocalTime clockInTime;
    @ApiModelProperty(value = "下班时间", example = "17:30:00")
    private LocalTime clockOutTime;
    @ApiModelProperty(value = "状态 1-正常 2-迟到 3-早退 4-缺卡", example = "1")
    private String status;
    @ApiModelProperty(value = "备注")
    private String remark;
}