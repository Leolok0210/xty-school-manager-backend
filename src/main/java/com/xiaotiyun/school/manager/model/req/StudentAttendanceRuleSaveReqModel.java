package com.xiaotiyun.school.manager.model.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalTime;
import java.util.List;

@Data
public class StudentAttendanceRuleSaveReqModel {
    @ApiModelProperty(value = "学校ID", required = true)
    @NotNull(message = "学校ID不能为空")
    private Long schoolId;

    @ApiModelProperty(value = "规则名称", required = true)
    @NotBlank(message = "规则名称不能为空")
    private String ruleName;

    @ApiModelProperty(value = "适用级组", required = true)
    @Valid
    @NotEmpty(message = "适用级组不能为空")
    private List<Long> gradeIds;

    @NotNull(message = "上午入校时间不能为空")
    @ApiModelProperty(value = "上午入校时间", required = true)
    private LocalTime morningInTime;

    @NotNull(message = "下午离校时间不能为空")
    @ApiModelProperty(value = "下午离校时间", required = true)
    private LocalTime afternoonOutTime;
}