package com.xiaotiyun.school.manager.model.req;

import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@ApiModel("学年成绩统计入参")
public class GradeYearReqModel {
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
    @ApiModelProperty(value = "班级ID", required = true, example = "1")
    private Long classId;

    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty(value = "学段ID，若为学年总结传0", required = true, example = "1")
    private Long semesterId;

    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty(value = "成绩类型，0-平时、1-考试", required = true, example = "1")
    private Integer gradeType;

    private Long userId;
} 