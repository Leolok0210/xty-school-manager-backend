package com.xiaotiyun.school.manager.model.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class UserRewardAddReqModel {
    @NotNull(message = "模板id不能为空")
    @ApiModelProperty(value = "模板id", required = true)
    private Long templateId;
    @NotNull(message = "奖励类型不能为空")
    @ApiModelProperty(value = "奖励类型,0-常规类型,1-竞赛奖励类型", required = true)
    private Integer registerType;
    @NotNull(message = "流程定义id不能为空")
    @ApiModelProperty(value = "流程定义id", required = true)
    private Long definitionId;
    @NotBlank(message = "所属学年不能为空")
    @ApiModelProperty(value = "所属学年", required = true)
    private String sid;
    @NotNull(message = "学期不能为空")
    @ApiModelProperty(value = "学期", required = true)
    private Long term;
    @NotNull(message = "类型不能为空")
    @ApiModelProperty(value = "类型 1奖励 2惩罚", required = true)
    private Integer type;
    @NotEmpty(message = "学生奖惩信息不能为空")
    @ApiModelProperty("学生奖惩信息")
    @Valid
    private List<UserRewardAddStudentReqModel> studentInfos;
    @ApiModelProperty(value = "审批人信息")
    private List<ActApprovalInstancePreviewApproverReqModel> approver;
}