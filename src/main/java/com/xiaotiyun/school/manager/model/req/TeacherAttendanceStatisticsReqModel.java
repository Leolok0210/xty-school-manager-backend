package com.xiaotiyun.school.manager.model.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@ApiModel("考勤统计查询参数")
public class TeacherAttendanceStatisticsReqModel {
    @NotNull(message = "学校id不能为空")
    @ApiModelProperty(value = "学校id")
    private Long schoolId;

    @NotNull(message = "教师ID不能为空")
    @ApiModelProperty(value = "教师ID")
    private Long teacherId;

    @NotNull(message = "开始日期不能为空")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "开始日期", example = "2023-10-01")
    private LocalDate startDate;

    @NotNull(message = "结束日期不能为空")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "结束日期", example = "2023-10-31")
    private LocalDate endDate;
}