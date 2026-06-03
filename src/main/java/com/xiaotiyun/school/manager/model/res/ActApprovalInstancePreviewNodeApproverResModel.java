package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ActApprovalInstancePreviewNodeApproverResModel {
    @ApiModelProperty(value = "审批人id")
    private Long userId;
    @ApiModelProperty(value = "审批人姓名")
    private String userName;
}