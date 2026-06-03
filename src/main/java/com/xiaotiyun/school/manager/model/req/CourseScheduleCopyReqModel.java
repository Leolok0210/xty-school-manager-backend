package com.xiaotiyun.school.manager.model.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

@Data
public class CourseScheduleCopyReqModel {
    @NotNull(message = "学段ID不能为空")
    @ApiModelProperty(value = "学段ID", required = true)
    private Long periodId;

    @NotNull(message = "班级id不能为空")
    @ApiModelProperty(value = "班级id(复制源班级id)", required = true)
    private Long classId;

    @NotNull(message = "开始日期不能为空")
    @ApiModelProperty(value = "开始日期(复制源课表开始时间)", required = true)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @NotNull(message = "结束日期不能为空")
    @ApiModelProperty(value = "结束日期(复制源课表结束时间)", required = true)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    @ApiModelProperty(value = "需要复制的周时间信息", required = true)
    @Valid
    @NotEmpty(message = "需要复制的周时间信息不能为空")
    private List<CourseScheduleDateCopyReqModel> dateList;
}