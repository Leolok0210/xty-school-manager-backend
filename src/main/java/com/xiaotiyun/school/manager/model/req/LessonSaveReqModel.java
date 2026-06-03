package com.xiaotiyun.school.manager.model.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalTime;

@Data
@ApiModel("课节请求参数")
public class LessonSaveReqModel {

    @NotBlank(message = "课节名称不能为空")
    @Size(max = 10, message = "课节名称最长10个字符")
    @ApiModelProperty(value = "课节名称", required = true)
    private String name;

    @NotNull(message = "级组id不能为空")
    @ApiModelProperty(value = "级组id", required = true)
    private Long gradeId;

    @ApiModelProperty(value = "开始时间", required = true)
    @NotNull(message = "开始时间不能为空")
    @DateTimeFormat(pattern = "HH:mm:ss")
    private LocalTime startTime;

    @ApiModelProperty(value = "结束时间", required = true)
    @NotNull(message = "结束时间不能为空")
    @DateTimeFormat(pattern = "HH:mm:ss")
    private LocalTime endTime;
} 