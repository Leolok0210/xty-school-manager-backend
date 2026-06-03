package com.xiaotiyun.school.manager.model.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalTime;

@Data
@ApiModel("教师考勤请求参数")
public class TeacherAttendanceUpdateReqModel {
    @ApiModelProperty(value = "卡号", example = "T2023001")
    private String cardNumber;

    @NotNull(message = "上班时间不能为空")
    @ApiModelProperty(value = "上班时间", required = true)
    private LocalTime clockInTime;

    @NotNull(message = "下班时间不能为空")
    @ApiModelProperty(value = "下班时间", required = true)
    private LocalTime clockOutTime;

    @ApiModelProperty(value = "备注")
    private String remark;
} 