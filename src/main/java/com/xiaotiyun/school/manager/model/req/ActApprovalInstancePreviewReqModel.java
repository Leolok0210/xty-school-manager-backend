package com.xiaotiyun.school.manager.model.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class ActApprovalInstancePreviewReqModel {
    @NotNull(message = "模板id不能为空")
    @ApiModelProperty(value = "模板id", required = true)
    private Long templateId;
    @NotNull(message = "流程定义id不能为空")
    @ApiModelProperty(value = "流程定义id", required = true)
    private Long definitionId;
    @ApiModelProperty(value = "申请天数")
    private Float applyDays;
    @ApiModelProperty(value = "请假类型（请假流程时传）")
    private Integer leaveType;
    @ApiModelProperty(value = "审批人信息")
    private List<ActApprovalInstancePreviewApproverReqModel> approver;
}