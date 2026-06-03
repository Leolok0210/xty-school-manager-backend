package com.xiaotiyun.school.manager.model.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 获取无课程学生人数请求模型
 */
@Data
@ApiModel("获取无课程学生人数请求模型")
public class ActivityStudentApplyReportNoCourseCountReqModel {

    @NotNull(message = "活动ID不能为空")
    @ApiModelProperty(value = "活动ID", required = true)
    private Long activityId;
} 