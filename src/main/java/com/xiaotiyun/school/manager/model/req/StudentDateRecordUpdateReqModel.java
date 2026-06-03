package com.xiaotiyun.school.manager.model.req;

import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel(description = "学生日期记录请求模型")
public class StudentDateRecordUpdateReqModel {
    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty(value = "记录ID", example = "1", required = true)
    private Long id;

    @ApiModelProperty(value = "学校ID", example = "11")
    private Long schoolId;

    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty(value = "学生ID", example = "201", required = true)
    private Long studentId;

    @ApiModelProperty(value = "入学时间", example = "2023-10-01 08:00:00")
    private String inTime;

    @ApiModelProperty(value = "退学时间", example = "2023-10-01 17:00:00")
    private String outTime;

    @ApiModelProperty(value = "退学原因", example = "会议")
    private String outReason;

    @ApiModelProperty(value = "升留级情况", example = "升级、留级")
    private String escalationSituation;
}