package com.xiaotiyun.school.manager.model.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class UserRewardImportReqModel {
    @NotNull(message = "上传文件不能为空")
    @ApiModelProperty(value = "Excel文件", required = true)
    private MultipartFile uploadFile;
    @NotNull(message = "模板id不能为空")
    @ApiModelProperty(value = "模板id", required = true)
    private Long templateId;
    @NotNull(message = "流程定义id不能为空")
    @ApiModelProperty(value = "流程定义id", required = true)
    private Long definitionId;
    @NotNull(message = "所属学年不能为空")
    @ApiModelProperty(value = "所属学年", required = true)
    private String sid;
    @NotNull(message = "学期不能为空")
    @ApiModelProperty(value = "学期", required = true)
    private Long term;
    @NotNull(message = "类型不能为空")
    @ApiModelProperty(value = "类型 1奖励 2惩罚", required = true)
    private Integer type;
    @ApiModelProperty("审批人信息")
    private List<ActApprovalInstancePreviewApproverReqModel> approver;
}