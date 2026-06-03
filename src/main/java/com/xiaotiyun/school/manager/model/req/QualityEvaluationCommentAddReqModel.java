package com.xiaotiyun.school.manager.model.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class QualityEvaluationCommentAddReqModel {
    @ApiModelProperty(value = "评语ID 编辑时传入",required = true)
    private Long id;
    @NotNull(message = "班级ID不能为空")
    @ApiModelProperty(value = "班级ID", required = true)
    private Long classId;

    @NotNull(message = "学生ID不能为空")
    @ApiModelProperty(value = "学生ID", required = true)
    private Long studentId;

    @NotNull(message = "老师ID不能为空")
    @ApiModelProperty(value = "老师ID", required = true)
    private Long teacherId;

    @NotNull(message = "评语模板不能为空")
    @ApiModelProperty(value = "评语模板", required = true)
    private String comment;
}