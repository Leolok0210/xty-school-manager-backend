package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ActProcessTemplateListResModel {
    @ApiModelProperty("流程id")
    private Long templateId;
    @ApiModelProperty("流程定义id")
    private Long definitionId;
    @ApiModelProperty("审批名称")
    private String processName;
    @ApiModelProperty("审批说明")
    private String processDesc;
}