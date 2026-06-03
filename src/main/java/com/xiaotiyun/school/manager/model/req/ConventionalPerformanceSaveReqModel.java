package com.xiaotiyun.school.manager.model.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class ConventionalPerformanceSaveReqModel {
    @NotBlank(message = "所属学年不能为空")
    @ApiModelProperty(value = "所属学年", required = true)
    private String sid;
    @NotNull(message = "学期不能为空")
    @ApiModelProperty(value = "学期", required = true)
    private Long term;
    @NotNull(message = "班级id不能为空")
    @ApiModelProperty(value = "班级id", required = true)
    private Long classId;
    @NotEmpty(message = "学生常规表现信息不能为空")
    @ApiModelProperty(value = "学生常规表现信息", required = true)
    @Valid
    private List<ConventionalPerformanceAddStudentReqModel> studentInfos;
} 