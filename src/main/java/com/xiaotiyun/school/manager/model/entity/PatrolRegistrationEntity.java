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
@TableName("patrol_registration")
@ApiModel(description = "巡堂登记实体对象")
public class PatrolRegistrationEntity extends BaseEntity {
    @NotNull(message = LanguageConstants.PARAM_ERROR)
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
    @TableField("lesson_period_id")
    @ApiModelProperty(value = "课节ID", example = "1")
    private Long lessonPeriodId;

    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @TableField("registration_id")
    @ApiModelProperty(value = "巡堂记录内容ID", example = "1")
    private Long registrationId;

    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @TableField("registration_content")
    @ApiModelProperty(value = "登记内容", example = "未佩戴校徽")
    private String registrationContent;

    @TableField("registrant_id")
    @ApiModelProperty(value = "登记ID", example = "1")
    private Long registrantId;

    @TableField("registrant")
    @ApiModelProperty(value = "登记人", example = "李四")
    private String registrant;
}