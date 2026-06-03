package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ActApprovalInstancePendingPageResModel extends ActApprovalInstanceInitiatedPageResModel{
    @ApiModelProperty(value = "任务id")
    private Long taskId;
    @ApiModelProperty(value = "发起人姓名")
    private String startUserName;
}