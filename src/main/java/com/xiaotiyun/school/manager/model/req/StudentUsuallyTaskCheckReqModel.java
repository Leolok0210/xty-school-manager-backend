package com.xiaotiyun.school.manager.model.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class StudentUsuallyTaskCheckReqModel {
    @NotBlank(message = "学年不能为空")
    @ApiModelProperty(value = "学年", required = true)
    private String schoolYear;
    @NotNull(message = "次数不能为空")
    @ApiModelProperty(value = "次数", required = true)
    private Integer frequency;
    @NotNull(message = "学段id不能为空")
    @ApiModelProperty(value = "学段id", required = true)
    private Long periodId;
    @NotNull(message = "班级id不能为空")
    @ApiModelProperty(value = "班级id", required = true)
    private Long classId;
    @NotNull(message = "科目id不能为空")
    @ApiModelProperty(value = "科目id", required = true)
    private Long subjectId;
}