package com.xiaotiyun.school.manager.model.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
public class StudentAttendanceReportReqModel {
    @NotBlank(message = "学年不能为空")
    @ApiModelProperty(value = "学年", required = true)
    private String schoolYear;

    @NotNull(message = "班级id不能为空")
    @ApiModelProperty(value = "班级id", required = true)
    private Long classId;

    @NotNull(message = "学段id不能为空")
    @ApiModelProperty(value = "学段id", required = true)
    private Long periodId;

    @NotNull(message = "查询开始时间不能为空")
    @ApiModelProperty(value = "查询开始时间", required = true)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate queryStartDate;

    @NotNull(message = "查询结束时间不能为空")
    @ApiModelProperty(value = "查询结束时间", required = true)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate queryEndDate;

    @ApiModelProperty(value = "学生id")
    private Long studentId;
}