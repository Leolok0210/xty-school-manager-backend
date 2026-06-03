package com.xiaotiyun.school.manager.model.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDateTime;

@Data
@ApiModel("教师请假请求参数")
public class TeacherLeaveSaveReqModel {
    @NotNull(message = "学校ID不能为空")
    @ApiModelProperty(value = "学校ID", required = true)
    private Long schoolId;

    @NotNull(message = "教师ID不能为空")
    @ApiModelProperty(value = "教师ID", required = true)
    private Long teacherId;

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
} 