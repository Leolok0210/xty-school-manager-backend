package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@ApiModel("学段信息")
public class SemesterResModel {
    @ApiModelProperty("ID")
    private Long id;

    @ApiModelProperty("学年(格式:2025-2026)")
    private String schoolYear;

    @ApiModelProperty("学部(1:幼稚园 2:小学 3:中学)")
    private Integer department;

    @ApiModelProperty("学段名称")
    private String name;

    @ApiModelProperty("开始时间")
    private LocalDateTime startTime;

    @ApiModelProperty("结束时间")
    private LocalDateTime endTime;
} 