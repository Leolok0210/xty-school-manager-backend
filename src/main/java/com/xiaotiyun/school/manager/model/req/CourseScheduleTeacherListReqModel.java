package com.xiaotiyun.school.manager.model.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
public class CourseScheduleTeacherListReqModel {
    @NotNull(message = "学段id不能为空")
    @ApiModelProperty(value = "学段id", required = true)
    private Long periodId;

    @NotNull(message = "教师id不能为空")
    @ApiModelProperty(value = "教师id", required = true)
    private Long teacherId;

    @NotNull(message = "开始日期不能为空")
    @ApiModelProperty(value = "开始日期", required = true)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @NotNull(message = "结束日期不能为空")
    @ApiModelProperty(value = "结束日期", required = true)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;
}