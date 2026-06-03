package com.xiaotiyun.school.manager.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.xiaotiyun.school.manager.basic.common.BaseEntity;
import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("big_little_rest")
@ApiModel(description = "大息小息表現登記实体对象")
public class BigLittleRestEntity extends BaseEntity {
    @TableField("school_id")
    @ApiModelProperty(value = "学校ID", example = "1")
    private Long schoolId;

    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @TableField("school_year")
    @ApiModelProperty(value = "学年", example = "2023-2024")
    private String schoolYear;

    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @TableField("semester_id")
    @ApiModelProperty(value = "学段ID", example = "1")
    private Long semesterId;

    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @TableField("class_id")
    @ApiModelProperty(value = "班级ID", example = "1")
    private Long classId;

    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @TableField("student_id")
    @ApiModelProperty(value = "学生ID", example = "12345")
    private Long studentId;

    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @TableField("registration_date")
    @ApiModelProperty(value = "日期", example = "2023-10-01")
    private String registrationDate;

    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @TableField("type")
    @ApiModelProperty(value = "类型", example = "大息/小息")
    private String type;

    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @TableField("registration_id")
    @ApiModelProperty(value = "大息小息表現ID", example = "1")
    private Long registrationId;

    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @TableField("registration_content")
    @ApiModelProperty(value = "大息小息表現", example = "未佩戴校徽")
    private String registrationContent;

    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @TableField("registrant_id")
    @ApiModelProperty(value = "登记人Id", example = "1")
    private Long registrantId;

    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @TableField("registrant")
    @ApiModelProperty(value = "登记人", example = "李四")
    private String registrant;
}