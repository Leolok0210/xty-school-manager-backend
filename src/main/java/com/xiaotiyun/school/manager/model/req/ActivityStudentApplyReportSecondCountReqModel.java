package com.xiaotiyun.school.manager.model.req;

import com.xiaotiyun.school.manager.basic.common.PageReqModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

/**
 * 活动二次报名人数查询请求模型
 */
@Data
@ApiModel("活动二次报名人数查询请求")
public class ActivityStudentApplyReportSecondCountReqModel {

    @ApiModelProperty(value = "活动ID", required = true, example = "1")
    @NotNull(message = "活动ID不能为空")
    private Long activityId;
} 