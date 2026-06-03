package com.xiaotiyun.school.manager.model.req;

import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@ApiModel("欠交作业表现添加请求信息")
public class MissingAssignmentPerformanceAddReqModel {

    @ApiModelProperty("id")
    private Long id;
    @ApiModelProperty(value = "所属学年", required = true)
    private String sid;

    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty(value = "学期", required = true)
    private Long term;

    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty(value ="班级id", required = true)
    private Long classId;

    @ApiModelProperty("学校ID")
    private Long schoolId;

    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty(value = "学生id",required = true)
    private Long studentId;

    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty(value = "日期", required = true)
    private LocalDateTime date;

    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty(value = "科目", required = true)
    private Long subjectId;

    @Size(max = 200, message = LanguageConstants.PARAM_ERROR)
    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty(value = "作业描述",required = true)
    private String assignmentDescription;

    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty(value = "用户id",required = true)
    private Long userId;
}