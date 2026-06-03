package com.xiaotiyun.school.manager.model.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class ActProcessTemplateNodeReqModel {
    @NotNull(message = "节点类型不能为空")
    @ApiModelProperty(value = "节点类型（1.发起人;2.审批人;3.抄送人;4.条件分支;5.网关节点）", required = true)
    private Integer nodeType;
    @NotBlank(message = "节点code不能为空")
    @ApiModelProperty(value = "节点code(本流程中唯一,前端自定义4位大写字母)", required = true)
    private String nodeCode;
    @NotBlank(message = "节点名称不能为空")
    @ApiModelProperty(value = "节点名称(如：审批人)", required = true)
    private String nodeName;
    @NotBlank(message = "节点展示名称不能为空")
    @ApiModelProperty(value = "节点展示名称（如：时长 <= 3）", required = true)
    private String nodeDisplayName;
    @ApiModelProperty(value = "来源节点（上一节点code）")
    private String nodeFrom;
    @ApiModelProperty(value = "审批类型（1.人工审批；2.自动通过）")
    private Integer approverType;
    @ApiModelProperty(value = "节点配置(JSON格式)")
    private String config;
    @ApiModelProperty(value = "节点优先级（同一级节点顺序，条件节点必传）")
    private Integer priority;
    @ApiModelProperty(value = "多人审批方式（1.或签;2.会签）")
    private Integer multiApproveMode;
}