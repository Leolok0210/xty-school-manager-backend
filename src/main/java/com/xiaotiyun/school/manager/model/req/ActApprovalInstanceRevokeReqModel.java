package com.xiaotiyun.school.manager.model.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ActApprovalInstanceRevokeReqModel {
    @NotNull(message = "流程id不能为空")
    @ApiModelProperty(value = "流程id", required = true)
    private Long instanceId;
}