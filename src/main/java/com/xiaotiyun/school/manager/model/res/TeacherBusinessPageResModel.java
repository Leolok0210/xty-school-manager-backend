package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TeacherBusinessPageResModel {
    @ApiModelProperty("id")
    private Long id;
    @ApiModelProperty("教师id")
    private Long teacherId;
    @ApiModelProperty("教师名称")
    private String teacherName;
    @ApiModelProperty("开始时间")
    private LocalDateTime startTime;
    @ApiModelProperty("结束时间")
    private LocalDateTime endTime;
    @ApiModelProperty("公务事由")
    private String reason;
}