package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class ActApprovalInstancePreviewNodeResModel {
    @ApiModelProperty(value = "节点类型（1.发起人;2.审批人;3.抄送人;4.条件分支）")
    private Integer nodeType;
    @ApiModelProperty(value = "节点code")
    private String nodeCode;
    @ApiModelProperty(value = "节点名称")
    private String nodeName;
    @ApiModelProperty(value = "来源节点（上一节点code）")
    private String nodeFrom;
    @ApiModelProperty(value = "审批类型（1.人工审批；2.自动通过）")
    private Integer approverType;
    @ApiModelProperty(value = "审批人信息")
    private List<ActApprovalInstancePreviewNodeApproverResModel> approver;
}