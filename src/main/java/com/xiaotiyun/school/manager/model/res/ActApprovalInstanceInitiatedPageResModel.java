package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ActApprovalInstanceInitiatedPageResModel {
    @ApiModelProperty(value = "流程id")
    private Long instanceId;
    @ApiModelProperty(value = "业务id")
    private Long businessId;
    @ApiModelProperty(value = "发起时间")
    private LocalDateTime initiatedStartTime;
    @ApiModelProperty(value = "开始时间")
    private LocalDateTime startTime;
    @ApiModelProperty(value = "结束时间")
    private LocalDateTime endTime;
    @ApiModelProperty(value = "请假类型（1-事假，2-病假，3-年假，4-产假，5-陪产假，6-婚假，7-丧假，8-产检假，9-育儿假）")
    private Integer leaveType;
    @ApiModelProperty(value = "事由")
    private String reason;
    @ApiModelProperty(value = "附件信息")
    private List<String> files;
    @ApiModelProperty(value = "状态：1-审批中，2-已完成，3-已拒绝")
    private Integer status;
    @ApiModelProperty(value = "审批意见")
    private String comment;
}