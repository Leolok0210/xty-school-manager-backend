package com.xiaotiyun.school.manager.model.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
public class StudentGraduateExamTaskSaveReqModel {
    @ApiModelProperty(value = "学校id", required = true)
    @NotNull(message = "学校id不能为空")
    private Long schoolId;
    @NotBlank(message = "考试名称不能为空")
    @ApiModelProperty(value = "考试名称", required = true)
    private String name;
    @NotNull(message = "考试时间不能为空")
    @ApiModelProperty(value = "考试时间", required = true)
    private LocalDate testDate;
    @NotNull(message = "班级id不能为空")
    @ApiModelProperty(value = "班级id", required = true)
    private Long classId;
    @NotNull(message = "学段id不能为空")
    @ApiModelProperty(value = "学段id", required = true)
    private Long periodId;
    @NotNull(message = "科目id不能为空")
    @ApiModelProperty(value = "科目id", required = true)
    private Long subjectId;
    @ApiModelProperty(value = "备注")
    private String remark;
}