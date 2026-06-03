package com.xiaotiyun.school.manager.model.req;

import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel(description = "医护保健记录请求对象")
public class MedicalRecordReqModel {
    @ApiModelProperty(value = "学校ID", example = "1")
    private Long schoolId;

    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty(value = "学年", example = "2023-2024")
    private String schoolYear;

    @ApiModelProperty(value = "班级ID", example = "1")
    private Long classId;

    @ApiModelProperty(value = "学生ID", example = "12345")
    private Long studentId;

    @ApiModelProperty(value = "开始日期", example = "2023-10-01")
    private String startDate;

    @ApiModelProperty(value = "结束日期", example = "2023-10-31")
    private String endDate;

    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty(value = "页码", example = "1")
    private Integer pageNum;

    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty(value = "每页大小", example = "10")
    private Integer pageSize;
}