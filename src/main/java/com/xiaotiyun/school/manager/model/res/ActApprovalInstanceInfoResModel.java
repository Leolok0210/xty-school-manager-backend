package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ActApprovalInstanceInfoResModel {
    @ApiModelProperty(value = "用户姓名")
    private String userName;
    @ApiModelProperty(value = "部门名称")
    private String depName;
    @ApiModelProperty(value = "开始时间")
    private LocalDateTime startTime;
    @ApiModelProperty(value = "结束时间")
    private LocalDateTime endTime;
    @ApiModelProperty(value = "请假类型（1-事假，2-病假，3-年假，4-产假，5-陪产假，6-婚假，7-丧假，8-产检假，9-育儿假）")
    private Integer leaveType;
    @ApiModelProperty(value = "事由")
    private String reason;
    @ApiModelProperty(value = "附件")
    private List<String> files;
    @ApiModelProperty(value = "当前状态（1-审批中，2-已通过，3-已拒绝）")
    private Integer status;
    @ApiModelProperty(value = "发起时间")
    private LocalDateTime initiatedStartTime;
    @ApiModelProperty(value = "节点信息")
    private List<ActApprovalInstanceInfoNodeResModel> nodes;
}