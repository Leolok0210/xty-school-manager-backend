package com.xiaotiyun.school.manager.model.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class StudentUsuallyScoreUpdateReqModel {
    @ApiModelProperty(value = "平时分登记id", required = true)
    @NotNull(message = "平时分登记id不能为空")
    private Long taskId;
    @NotNull(message = "学生id不能为空")
    @ApiModelProperty(value = "学生id", required = true)
    private Long studentId;
    @NotNull(message = "成绩不能为空")
    @ApiModelProperty(value = "成绩*100", required = true)
    private Integer score;
}