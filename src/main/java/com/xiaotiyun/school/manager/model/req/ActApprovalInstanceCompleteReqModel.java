package com.xiaotiyun.school.manager.model.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ActApprovalInstanceCompleteReqModel {
    @NotNull(message = "任务id不能为空")
    @ApiModelProperty(value = "任务id", required = true)
    private Long taskId;
    @NotNull(message = "审批结果不能为空")
    @ApiModelProperty(value = "审批结果：1-同意，2-拒绝，3-转办", required = true)
    private Integer approvalResult;
    @ApiModelProperty(value = "审批意见")
    private String comment;
}