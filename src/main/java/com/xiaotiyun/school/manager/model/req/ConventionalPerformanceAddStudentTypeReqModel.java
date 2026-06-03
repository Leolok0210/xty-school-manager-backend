package com.xiaotiyun.school.manager.model.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ConventionalPerformanceAddStudentTypeReqModel {
    @NotNull(message = "类型不能为空")
    @ApiModelProperty(value = "类型(1.上课违规;2.欠作业;3.仪表不符;5.欠课本;7.欠回条)", required = true)
    private Integer type;
    @NotNull(message = "次数不能为空")
    @ApiModelProperty(value = "次数", required = true)
    private Integer frequency;
    @ApiModelProperty(value = "备注")
    private String remark;
}