package com.xiaotiyun.school.manager.model.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class StudentUsuallyScoreCheckReqModel {
    @NotNull(message = "学校id不能为空")
    @ApiModelProperty(value = "学校id", required = true)
    private Long schoolId;
    @ApiModelProperty(value = "学年")
    private String schoolYear;
    @ApiModelProperty(value = "学段id")
    private Long periodId;
    @ApiModelProperty(value = "班级id")
    private Long classId;
    @ApiModelProperty(value = "科目id")
    private Long subjectId;

    private Long userId;
}