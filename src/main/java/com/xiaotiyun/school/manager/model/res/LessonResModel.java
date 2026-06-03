package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalTime;

@Data
public class LessonResModel {
    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty(value = "课节名称")
    private String name;

    @ApiModelProperty(value = "开始时间")
    private LocalTime startTime;

    @ApiModelProperty(value = "结束时间")
    private LocalTime endTime;
}