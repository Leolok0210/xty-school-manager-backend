package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ActProcessTemplateNodeResModel {
    @ApiModelProperty(value = "节点类型（1.发起人;2.审批人;3.抄送人;4.条件分支;5.网关节点）")
    private Integer nodeType;
    @ApiModelProperty(value = "节点code(本流程中唯一,前端自定义)")
    private String nodeCode;
    @ApiModelProperty(value = "节点名称(如：审批人)")
    private String nodeName;
    @ApiModelProperty(value = "节点展示名称（如：时长 <= 3）")
    private String nodeDisplayName;
    @ApiModelProperty(value = "来源节点（上一节点code）")
    private String nodeFrom;
    @ApiModelProperty(value = "审批类型（1.人工审批；2.自动通过）")
    private Integer approverType;
    @ApiModelProperty(value = "节点配置(JSON格式)")
    private String config;
    @ApiModelProperty(value = "节点优先级（同一级节点顺序）")
    private Integer priority;
    @ApiModelProperty(value = "多人审批方式（1.或签;2.会签）")
    private Integer multiApproveMode;
}