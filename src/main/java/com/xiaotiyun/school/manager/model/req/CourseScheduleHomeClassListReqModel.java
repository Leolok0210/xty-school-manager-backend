package com.xiaotiyun.school.manager.model.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
public class CourseScheduleHomeClassListReqModel {
    @NotNull(message = "学年不能为空")
    @ApiModelProperty(value = "学年", required = true)
    private String schoolYear;

    @NotNull(message = "学段id不能为空")
    @ApiModelProperty(value = "学段id", required = true)
    private Long periodId;

    @NotNull(message = "级组id不能为空")
    @ApiModelProperty(value = "级组id", required = true)
    private Long gradeId;

    @NotNull(message = "课程日期不能为空")
    @ApiModelProperty(value = "课程日期", required = true)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate courseDate;
}