package com.xiaotiyun.school.manager.model.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class StudentAttendanceUpdateReqModel {
    @NotNull(message = "入校时间不能为空")
    @ApiModelProperty(value = "入校时间（上午）")
    private LocalTime morningInTime;
    @ApiModelProperty(value = "离校时间（上午）")
    private LocalTime morningOutTime;
    @ApiModelProperty(value = "入校时间（下午）")
    private LocalTime afternoonInTime;
    @NotNull(message = "离校时间不能为空")
    @ApiModelProperty(value = "离校时间（下午）")
    private LocalTime afternoonOutTime;
    @ApiModelProperty(value = "备注")
    private String remark;
}