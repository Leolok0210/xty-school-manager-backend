package com.xiaotiyun.school.manager.model.req;

import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel(description = "学生迟到月度统计请求对象")
public class StudentLateReportMonthReqModel {
    @ApiModelProperty(value = "学校ID", example = "1")
    private Long schoolId;

    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty(value = "学年", example = "2023-2024", required = true)
    private String schoolYear;

    @ApiModelProperty(value = "学部ID", example = "1")
    private Long department;

    @ApiModelProperty(value = "级组ID", example = "1")
    private Long gradeId;

    @ApiModelProperty(value = "班级ID", example = "1")
    private Long classId;

    @ApiModelProperty(value = "月份", example = "1-12")
    private Long queryMonth;

    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty(value = "页码", example = "1", required = true)
    private Integer pageNum;

    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty(value = "每页大小", example = "10", required = true)
    private Integer pageSize;
}
