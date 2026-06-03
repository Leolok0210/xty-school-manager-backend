package com.xiaotiyun.school.manager.model.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class TeacherLeaveStartReqModel {
    @NotNull(message = "请假类型不能为空")
    @Min(value = 1, message = "无效的请假类型")
    @Max(value = 9, message = "无效的请假类型")
    @ApiModelProperty(value = "请假类型（1-事假，2-病假，3-年假，4-产假，5-陪产假，6-婚假，7-丧假，8-产检假，9-育儿假）", required = true)
    private Integer leaveType;

    @NotNull(message = "开始时间不能为空")
    @ApiModelProperty(value = "开始时间", required = true)
    private LocalDateTime startTime;

    @NotNull(message = "结束时间不能为空")
    @ApiModelProperty(value = "结束时间", required = true)
    private LocalDateTime endTime;

    @NotBlank(message = "请假事由不能为空")
    @Size(max = 200, message = "请假事由最长200个字符")
    @ApiModelProperty(value = "请假事由", required = true)
    private String reason;

    @ApiModelProperty(value = "文件ID")
    private List<Long> fileIds;

    @NotNull(message = "模板id不能为空")
    @ApiModelProperty(value = "模板id", required = true)
    private Long templateId;

    @NotNull(message = "流程定义id不能为空")
    @ApiModelProperty(value = "流程定义id", required = true)
    private Long definitionId;

    @NotNull(message = "请假天数不能为空")
    @ApiModelProperty(value = "请假天数", required = true)
    private Float leaveDays;

    @ApiModelProperty(value = "审批人信息")
    private List<ActApprovalInstancePreviewApproverReqModel> approver;
} 