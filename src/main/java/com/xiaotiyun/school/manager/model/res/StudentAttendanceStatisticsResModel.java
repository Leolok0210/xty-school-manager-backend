package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class StudentAttendanceStatisticsResModel {
    @ApiModelProperty("学部(1:幼稚园 2:小学 3:中学)")
    private Integer department;
    @ApiModelProperty("学段名称")
    private String semesterName;
    @ApiModelProperty("次数")
    private Integer count;
}