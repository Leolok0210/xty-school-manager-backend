package com.xiaotiyun.school.manager.model.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class ActApprovalInstancePreviewApproverReqModel {
    @ApiModelProperty(value = "节点code")
    private String nodeCode;
    @ApiModelProperty(value = "审批人id（审批人自选时传）")
    private List<Long> approverIds;
    @ApiModelProperty(value = "抄送人id（抄送人自选时传）")
    private List<Long> copyIds;
}