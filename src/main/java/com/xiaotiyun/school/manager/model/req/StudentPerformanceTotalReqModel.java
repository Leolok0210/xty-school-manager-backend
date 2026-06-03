package com.xiaotiyun.school.manager.model.req;

import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class StudentPerformanceTotalReqModel {

    @ApiModelProperty("学校id")
    private Long schoolId;

    @ApiModelProperty("学生id")
    private Long studentId;

    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty("学年")
    private String schoolYear;

    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty("学期id")
    private Long semesterId;
}
