package com.xiaotiyun.school.manager.model.req;

import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel(description = "平时成绩类型关联请求类")
public class StudentUsuallyRuleReqModel {

    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty(value = "级组ID")
    private Long gradeGroupId;

    @ApiModelProperty(value = "科目ID")
    private Long subjectId;
}