package com.xiaotiyun.school.manager.model.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
public class ConventionalPerformanceUpdateReqModel {
    @NotBlank(message = "所属学年不能为空")
    @ApiModelProperty(value = "所属学年", required = true)
    private String sid;
    @NotNull(message = "学期不能为空")
    @ApiModelProperty(value = "学期", required = true)
    private Long term;
    @NotNull(message = "班级id不能为空")
    @ApiModelProperty(value = "班级id", required = true)
    private Long classId;
    @NotNull(message = "事件日期不能为空")
    @ApiModelProperty(value = "事件日期", example = "2024-10-01", required = true)
    private LocalDate date;
    @NotNull(message = "学生id不能为空")
    @ApiModelProperty(value = "学生id", required = true)
    private Long studentId;
    @NotNull(message = "类型不能为空")
    @ApiModelProperty(value = "类型(1.上课违规;2.欠作业;3.仪表不符;5.欠课本;7.欠回条)", required = true)
    private Integer type;
    @NotNull(message = "次数不能为空")
    @ApiModelProperty(value = "次数", required = true)
    private Integer frequency;
    @ApiModelProperty(value = "备注")
    private String remark;
} 