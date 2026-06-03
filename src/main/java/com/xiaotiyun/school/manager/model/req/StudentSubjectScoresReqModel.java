package com.xiaotiyun.school.manager.model.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel("学生各科成绩查询请求参数")
public class StudentSubjectScoresReqModel {

    @ApiModelProperty(value = "学段", required = true)
    @NotNull(message = "学段不能为空")
    private Long section;

    @ApiModelProperty(value = "班级", required = true)
    @NotNull(message = "班级不能为空")
    private Long classId;

    @ApiModelProperty(hidden = true)
    private Long schoolId;
}