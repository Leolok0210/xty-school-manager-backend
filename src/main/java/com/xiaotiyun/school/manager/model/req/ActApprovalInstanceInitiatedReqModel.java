package com.xiaotiyun.school.manager.model.req;

import com.xiaotiyun.school.manager.basic.common.PageReqModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ActApprovalInstanceInitiatedReqModel extends PageReqModel {
    @NotNull(message = "审批类型不能为空")
    @ApiModelProperty(value = "审批类型(1.教师请假；2.教师公务)", required = true)
    private Integer processType;
    @ApiModelProperty(value = "状态：1-审批中，2-已完成，3-已拒绝")
    private Integer status;
}