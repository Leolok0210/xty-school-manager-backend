package com.xiaotiyun.school.manager.model.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

@Data
public class ConventionalPerformanceAddStudentReqModel {
    @NotNull(message = "事件日期不能为空")
    @ApiModelProperty(value = "事件日期", example = "2024-10-01", required = true)
    private LocalDate date;
    @NotNull(message = "学生id不能为空")
    @ApiModelProperty(value = "学生id", required = true)
    private Long studentId;
    @NotEmpty(message = "类型信息不能为空")
    @ApiModelProperty(value = "类型信息", required = true)
    @Valid
    private List<ConventionalPerformanceAddStudentTypeReqModel> types;
}