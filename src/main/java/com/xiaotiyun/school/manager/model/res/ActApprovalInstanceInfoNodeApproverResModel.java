package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ActApprovalInstanceInfoNodeApproverResModel {
    @ApiModelProperty(value = "审批人姓名")
    private String userName;
    @ApiModelProperty(value = "审批结果：1-同意，2-拒绝")
    private Integer approvalResult;
    @ApiModelProperty(value = "审批时间")
    private LocalDateTime approvalTime;
    @ApiModelProperty(value = "审批意见")
    private String comment;
}