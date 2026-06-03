package com.xiaotiyun.school.manager.model.req;

import com.xiaotiyun.school.manager.basic.common.PageReqModel;
import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
public class StudentExamScoreReqModel extends PageReqModel {

    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty(value = "学段ID")
    private Long semesterId;

    @ApiModelProperty(value = "科目ID")
    private Long subjectId;

    @ApiModelProperty(value = "学生ID")
    private Long studentId;
}