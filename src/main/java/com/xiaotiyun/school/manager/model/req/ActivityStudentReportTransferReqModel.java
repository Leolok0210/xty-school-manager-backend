package com.xiaotiyun.school.manager.model.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 转课程请求模型
 */
@Data
@ApiModel("转课程请求模型")
public class ActivityStudentReportTransferReqModel {

    @NotNull(message = "活动ID不能为空")
    @ApiModelProperty(value = "活动ID", required = true)
    private Long activityId;

    @NotNull(message = "课程ID不能为空")
    @ApiModelProperty(value = "课程ID", required = true)
    private Long lensonId;

    @NotNull(message = "学生ID不能为空")
    @ApiModelProperty(value = "学生ID", required = true)
    private Long studentId;

//    @NotNull(message = "类型不能为空")
//    @ApiModelProperty(value = "类型（1.转入，2转班）", required = true)
//    private Integer type;

    @ApiModelProperty(value = "0-批量导入 1-移除 2-批量移除 3-分配 4-批量分配 5-转班 6-批量转班 7-转入 8-批量转入", required = true)
    private Integer opType;


    @NotNull(message = "schoolID不能为空")
    @ApiModelProperty(value = "schoolId", required = true)
    private Long schoolId;


    @NotNull(message = "页面来源不能为空")
    @ApiModelProperty(value = "页面来源，1-预先导入，2-已匹配，3-无课程，4-二次报名", required = true)
    private Integer source;
} 