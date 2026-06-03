package com.xiaotiyun.school.manager.model.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 我的课程列表请求模型
 */
@Data
@ApiModel("我的课程列表请求模型")
public class ActivityStudentApplyReportMyCourseReqModel {

    @NotNull(message = "学生ID不能为空")
    @ApiModelProperty(value = "学生ID", required = true)
    private Long studentId;

    @NotNull(message = "学年不能为空")
    @ApiModelProperty(value = "学年", required = true)
    private String schoolYear;
} 