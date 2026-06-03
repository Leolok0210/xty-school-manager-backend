package com.xiaotiyun.school.manager.model.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@ApiModel("新增学段请求")
public class SemesterAddReqModel {

    @ApiModelProperty(value = "id新增时不用填，编辑时必填")
    private Long id;

    @NotBlank(message = "学年不能为空")
    @ApiModelProperty(value = "学年(格式:2025-2026)", required = true)
    private String schoolYear;

    @NotNull(message = "学部不能为空")
    @ApiModelProperty(value = "学部(1:幼稚园 2:小学 3:中学)", required = true)
    private Integer department;

    @NotBlank(message = "学段名称不能为空")
    @Size(max = 50, message = "学段名称最长50个字")
    @ApiModelProperty(value = "学段名称", required = true)
    private String name;

    @NotNull(message = "开始时间不能为空")
    @ApiModelProperty(value = "开始时间", required = true)
    private LocalDateTime startTime;

    @NotNull(message = "结束时间不能为空")
    @ApiModelProperty(value = "结束时间", required = true)
    private LocalDateTime endTime;
} 