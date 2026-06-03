package com.xiaotiyun.school.manager.model.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class LessonListReqModel {
    @NotNull(message = "级组id不能为空")
    @ApiModelProperty(value = "级组id", required = true)
    private Long gradeId;
}