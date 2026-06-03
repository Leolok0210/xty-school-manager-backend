package com.xiaotiyun.school.manager.model.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class LessonCopyReqModel {
    @NotNull(message = "级组id不能为空")
    @ApiModelProperty(value = "级组id", required = true)
    private Long gradeId;

    @NotEmpty(message = "需要复制的级组id不能为空")
    @ApiModelProperty(value = "需要复制的级组id", required = true)
    private List<Long> copyGradeIds;
}