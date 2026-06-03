package com.xiaotiyun.school.manager.model.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import javax.validation.constraints.*;
import java.time.LocalDateTime;

@Data
@ApiModel("成绩录入时间设定保存请求")
public class GradeRecordTimeSettingSaveReqModel {
    
    @NotBlank(message = "学年不能为空")
    @ApiModelProperty(value = "学年", required = true, example = "2025-2026")
    private String schoolYear;
    
    @NotNull(message = "学段ID不能为空")
    @ApiModelProperty(value = "学段ID", required = true)
    private Long semesterId;
    
    @NotNull(message = "学部不能为空")
    @Min(value = 1, message = "学部值必须在1-3之间")
    @Max(value = 3, message = "学部值必须在1-3之间")
    @ApiModelProperty(value = "学部(1:幼稚园 2:小学 3:中学)", required = true, example = "1")
    private Integer department;
    
    @NotNull(message = "开始时间不能为空")
    @ApiModelProperty(value = "开始时间", required = true)
    private LocalDateTime startTime;
    
    @NotNull(message = "结束时间不能为空")
    @ApiModelProperty(value = "结束时间", required = true)
    private LocalDateTime endTime;
} 