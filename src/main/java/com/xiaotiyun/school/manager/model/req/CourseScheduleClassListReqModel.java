package com.xiaotiyun.school.manager.model.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
public class CourseScheduleClassListReqModel {
    @ApiModelProperty(value = "学年(学段id未传时学年必传)")
    private String schoolYear;

    @ApiModelProperty(value = "学段id(学年未传时学段id必传)")
    private Long periodId;

    @NotNull(message = "班级id不能为空")
    @ApiModelProperty(value = "班级id", required = true)
    private Long classId;

    @NotNull(message = "开始日期不能为空")
    @ApiModelProperty(value = "开始日期", required = true)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @NotNull(message = "结束日期不能为空")
    @ApiModelProperty(value = "结束日期", required = true)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;
}