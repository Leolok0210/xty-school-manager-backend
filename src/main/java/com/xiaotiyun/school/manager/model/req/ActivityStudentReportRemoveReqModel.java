package com.xiaotiyun.school.manager.model.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 活动匹配批量移除请求模型
 */
@Data
@ApiModel("活动匹配批量移除请求")
public class ActivityStudentReportRemoveReqModel {

    @NotEmpty(message = "学生ID列表不能为空")
    @ApiModelProperty(value = "学生ID列表", required = true)
    private List<Long> ids;

    @NotNull(message = "活动ID不能为空")
    @ApiModelProperty(value = "活动ID", required = true)
    private Long activityId;

    @ApiModelProperty(value = "0-批量导入 1-移除 2-批量移除 3-分配 4-批量分配 5-转班 6-批量转班 7-转入 8-批量转入", required = true)
    private Integer opType;

    @NotNull(message = "页面来源不能为空")
    @ApiModelProperty(value = "页面来源，1-预先导入，2-已匹配，3-无课程，4-二次报名", required = true)
    private Integer source;
} 