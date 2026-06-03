package com.xiaotiyun.school.manager.model.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@ApiModel("最高成绩")
public class TopScoreReqModel {
    
    @NotBlank(message = "学年不能为空")
    @ApiModelProperty(value = "学年", required = true, example = "2024-2025")
    private String schoolYear;

    @NotNull(message = "学部不能为空")
    @Min(value = 1, message = "学部值必须在1-3之间")
    @Max(value = 3, message = "学部值必须在1-3之间")
    @ApiModelProperty(value = "学部(1:幼稚园 2:小学 3:中学)", required = true, example = "1")
    private Integer department;

    @NotNull(message = "学段ID不能为空")
    @ApiModelProperty(value = "学段ID", required = true)
    private Long semesterId;
    @NotNull(message = "级组id不能为空")
    @ApiModelProperty(value = "级组id", required = true, example = "1")
    private Long groupId;

    private Long userId;
}