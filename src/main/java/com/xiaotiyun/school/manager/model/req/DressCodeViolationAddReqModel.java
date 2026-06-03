package com.xiaotiyun.school.manager.model.req;

import com.baomidou.mybatisplus.annotation.TableField;
import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel(description = "仪表不符登记新增请求对象")
public class DressCodeViolationAddReqModel {
    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty(value = "学年", example = "2023-2024", required = true)
    private String schoolYear;

    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty(value = "学段ID", example = "1", required = true)
    private Long semesterId;

    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty(value = "班级ID", example = "1", required = true)
    private Long classId;

    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty(value = "学生ID", example = "12345", required = true)
    private Long studentId;

    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty(value = "日期", example = "2023-10-01", required = true)
    private String violationDate;

    @ApiModelProperty(value = "备注ID", example = "预设参数ID")
    private Long remarkId;

    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty(value = "备注", example = "未佩戴校徽", required = true)
    private String remark;

}