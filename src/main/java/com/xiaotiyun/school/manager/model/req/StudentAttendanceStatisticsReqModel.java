package com.xiaotiyun.school.manager.model.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@ApiModel("学生出勤统计查询参数")
public class StudentAttendanceStatisticsReqModel {
    @NotNull(message = "学生id不能为空")
    @ApiModelProperty(value = "学生id", required = true)
    private Long studentId;

    @NotBlank(message = "学年不能为空")
    @ApiModelProperty(value = "学年", required = true)
    private String schoolYear;

    @ApiModelProperty(value = "出勤状态（0.正常;1.迟到;2.早退;3.缺卡;4.数据异常）")
    private Integer status;
}