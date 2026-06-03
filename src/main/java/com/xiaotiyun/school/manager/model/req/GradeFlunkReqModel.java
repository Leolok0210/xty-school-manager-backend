package com.xiaotiyun.school.manager.model.req;

import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@ApiModel("不合格成绩入参")
public class GradeFlunkReqModel {
    @ApiModelProperty(value = "学校ID")
    private Long schoolId;

    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty(value = "学年", required = true, example = "2024-2025")
    private String schoolYear;

    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @Min(value = 1, message = "学部值必须在1-3之间")
    @Max(value = 3, message = "学部值必须在1-3之间")
    @ApiModelProperty(value = "学部(1:幼稚园 2:小学 3:中学)", required = true, example = "1")
    private Integer department;


    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty(value = "级组id", required = true, example = "1")
    private Long groupId;

    private Long userId;
} 