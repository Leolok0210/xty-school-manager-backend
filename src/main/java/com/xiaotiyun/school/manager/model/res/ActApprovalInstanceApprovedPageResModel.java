package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ActApprovalInstanceApprovedPageResModel extends ActApprovalInstancePendingPageResModel{
    @ApiModelProperty(value = "完成时间")
    private LocalDateTime completeTime;
}