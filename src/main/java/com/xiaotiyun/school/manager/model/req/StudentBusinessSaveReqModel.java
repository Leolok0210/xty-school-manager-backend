package com.xiaotiyun.school.manager.model.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@ApiModel("学生公务请求参数")
public class StudentBusinessSaveReqModel {
    @NotBlank(message = "学年不能为空")
    @ApiModelProperty(value = "学年", required = true)
    private String schoolYear;

    @NotNull(message = "班级id不能为空")
    @ApiModelProperty(value = "班级id", required = true)
    private Long classId;

    @NotNull(message = "学生ID不能为空")
    @ApiModelProperty(value = "学生ID", required = true)
    private Long studentId;

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
} 