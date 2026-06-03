package com.xiaotiyun.school.manager.model.req;

import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
public class ActProcessTemplateSaveReqModel {
    @NotBlank(message = "审批名称不能为空")
    @Size(max = 50, message = "名称最长不超过50字")
    @ApiModelProperty(value = "审批名称", required = true)
    private String processName;
    @Size(max = 200, message = "审批说明最长不超过200字")
    @ApiModelProperty(value = "审批说明")
    private String processDesc;
    @NotNull(message = "审批类型不能为空")
    @ApiModelProperty(value = "审批类型(1.教师请假；2.教师公务)", required = true)
    private Integer processType;
    @NotNull(message = "规则设置不能为空")
    @ApiModelProperty(value = "规则设置（1.仅首个节点需审批；2.每个节点都需审批；3.仅连续审批自动同意）", required = true)
    private Integer ruleSetting;
    @ApiModelProperty(value = "发起人范围json(为空时表示所有人都可发起)")
    private String initiatorScope;
    @NotEmpty(message = "节点信息不能为空")
    @ApiModelProperty(value = "节点信息")
    @Valid
    private List<ActProcessTemplateNodeReqModel> nodes;
}