package com.xiaotiyun.school.manager.model.req;

import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel(description = "余暇活动记录首页请求对象")
public class LeisureActivityRecordIndexReqModel {
    @ApiModelProperty(value = "学校ID")
    private Long schoolId;

    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty(value = "学年")
    private String schoolYear;

    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty(value = "级组")
    private Long groupId;

    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty(value = "学生ID")
    private Long studentId;
}