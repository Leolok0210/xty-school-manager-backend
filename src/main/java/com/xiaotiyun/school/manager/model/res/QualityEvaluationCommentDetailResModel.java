package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class QualityEvaluationCommentDetailResModel {
    @ApiModelProperty(value = "主键ID")
    private Long id;

    @ApiModelProperty(value = "班级ID")
    private Long classId;

    @ApiModelProperty(value = "学生ID")
    private Long studentId;

    @ApiModelProperty(value = "老师ID")
    private Long teacherId;

    @ApiModelProperty(value = "评语模板")
    private String comment;
}