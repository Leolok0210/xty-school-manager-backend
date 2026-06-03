package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class ActProcessTemplateInfoResModel {
    @ApiModelProperty(value = "主键ID")
    private Long id;
    @ApiModelProperty(value = "审批名称")
    private String processName;
    @ApiModelProperty(value = "审批说明")
    private String processDesc;
    @ApiModelProperty(value = "审批类型(1.教师请假；2.教师公务)")
    private Integer processType;
    @ApiModelProperty(value = "发起人范围json(为空时表示所有人都可发起)")
    private String initiatorScope;
    @ApiModelProperty(value = "规则设置（1.仅首个节点需审批；2.每个节点都需审批；3.仅连续审批自动同意）")
    private Integer ruleSetting;
    @ApiModelProperty(value = "节点信息")
    private List<ActProcessTemplateNodeResModel> nodes;
}