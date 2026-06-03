package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

@Data
public class SchoolCalendarDateTypeResModel {
    @ApiModelProperty("ID")
    private Long id;

    @ApiModelProperty("类型(1:工作日,2:双休日,3:假期)")
    private Integer type;

    @ApiModelProperty("适用类型(1:老师,2:学生)")
    private Integer applyType;

    @ApiModelProperty("日期")
    private LocalDate calendarDate;
} 