package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class SchoolCalendarV230ResModel {
    @ApiModelProperty("校历ID")
    private Long id;

    @ApiModelProperty("学校ID")
    private Long schoolId;

    @ApiModelProperty("校历名称")
    private String calendarName;

    @ApiModelProperty("开始日期")
    private LocalDate startDate;

    @ApiModelProperty("结束日期")
    private LocalDate endDate;

    @ApiModelProperty("事项列表")
    private List<SchoolCalendarEventResModel> eventList;

    @ApiModelProperty("日期属性列表")
    private List<SchoolCalendarDateTypeResModel> dateTypeList;
}