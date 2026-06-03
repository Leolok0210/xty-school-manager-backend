package com.xiaotiyun.school.manager.model.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class TeacherBusinessStartReqModel {
    @NotBlank(message = "公务事由不能为空")
    @Size(max = 200, message = "公务事由最长200个字符")
    @ApiModelProperty(value = "公务事由", required = true)
    private String reason;

    @NotNull(message = "开始时间不能为空")
    @ApiModelProperty(value = "开始时间", required = true)
    private LocalDateTime startTime;

    @NotNull(message = "结束时间不能为空")
    @ApiModelProperty(value = "结束时间", required = true)
    private LocalDateTime endTime;

    @ApiModelProperty(value = "文件ID")
    private List<Long> fileIds;

    @NotNull(message = "模板id不能为空")
    @ApiModelProperty(value = "模板id", required = true)
    private Long templateId;

    @NotNull(message = "流程定义id不能为空")
    @ApiModelProperty(value = "流程定义id", required = true)
    private Long definitionId;

    @NotNull(message = "公务天数不能为空")
    @ApiModelProperty(value = "公务天数", required = true)
    private Float businessDays;

    @ApiModelProperty(value = "审批人信息")
    private List<ActApprovalInstancePreviewApproverReqModel> approver;
} 