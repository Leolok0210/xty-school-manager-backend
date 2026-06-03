package com.xiaotiyun.school.manager.model.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ActApprovalInstanceInfoReqModel {
    @NotNull(message = "流程id不能为空")
    @ApiModelProperty(value = "流程id", required = true)
    private Long instanceId;
    @NotNull(message = "业务id不能为空")
    @ApiModelProperty(value = "业务id", required = true)
    private Long businessId;
    @NotNull(message = "审批类型不能为空")
    @ApiModelProperty(value = "审批类型(1.教师请假；2.教师公务)", required = true)
    private Integer processType;
}