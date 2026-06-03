package com.xiaotiyun.school.manager.model.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class ExportRecordReqModel {
    @ApiModelProperty("关联id 传入班级id")
    private Long classId;
    @ApiModelProperty("页码")
    private int pageNum;
    @ApiModelProperty("页大小")
    private int pageSize;
    @ApiModelProperty("学年 没有传入空")
    private String schoolYear;
    @ApiModelProperty("学期 没有传入空")
    private Long term;
    @ApiModelProperty("学部 没有传入空")
    private String department;
    @ApiModelProperty("开始时间 时间戳")
    private Long startTime;
    @ApiModelProperty("结束时间 时间戳")
    private Long endTime;

}
