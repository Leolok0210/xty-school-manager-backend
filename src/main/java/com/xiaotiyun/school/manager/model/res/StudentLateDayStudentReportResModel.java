package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "学生迟到统计-天-返回当天对象")
public class StudentLateDayStudentReportResModel {
    @ApiModelProperty(value = "迟到学生ID", example = "1")
    private Long studentId;

    @ApiModelProperty(value = "迟到学生姓名", example = "张三")
    private String studentName;

    @ApiModelProperty(value = "班内号", example = "1")
    private Long seatNumber;

    @ApiModelProperty(value = "入校时间", example = "08:00:00")
    private String morningInTime;
}
