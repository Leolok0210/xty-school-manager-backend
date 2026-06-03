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
@TableName("student_medical_record")
@ApiModel(description = "学生医护保健记录实体对象")
public class StudentMedicalRecordEntity extends BaseEntity {
    @ApiModelProperty(value = "学校ID", example = "1")
    private Long schoolId;

    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @TableField("school_year")
    @ApiModelProperty(value = "学年", example = "2023-2024", required = true)
    private String schoolYear;

    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @TableField("class_id")
    @ApiModelProperty(value = "班级ID", example = "1", required = true)
    private Long classId;

    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @TableField("student_id")
    @ApiModelProperty(value = "学生ID", example = "12345", required = true)
    private Long studentId;

    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @TableField("student_name")
    @ApiModelProperty(value = "学生姓名", example = "张三", required = true)
    private String studentName;

    @TableField("chief_complaint")
    @ApiModelProperty(value = "主诉现病史", example = "其他原因")
    private String chiefComplaint;

    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @TableField("treatment")
    @ApiModelProperty(value = "处理", example = "服用退烧药", required = true)
    private String treatment;

    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @TableField("notes")
    @ApiModelProperty(value = "备注", example = "按时复查", required = true)
    private String notes;

    @TableField("consultation_date")
    @ApiModelProperty(value = "求诊时间", example = "2023-10-01 10:00:00")
    private String consultationDate;

    @TableField("temperature")
    @ApiModelProperty(value = "体温", example = "36.5")
    private String temperature;

    @TableField("fever")
    @ApiModelProperty(value = "是否发热", example = "0")
    private boolean fever;

    @TableField("cough")
    @ApiModelProperty(value = "是否咳嗽", example = "0")
    private boolean cough;

    @TableField("runny_nose")
    @ApiModelProperty(value = "是否流涕", example = "0")
    private boolean runnyNose;

    @TableField("sore_throat")
    @ApiModelProperty(value = "是否咽痛", example = "0")
    private boolean soreThroat;

    @TableField("dizziness")
    @ApiModelProperty(value = "是否头晕", example = "0")
    private boolean dizziness;

    @TableField("headache")
    @ApiModelProperty(value = "是否头痛", example = "0")
    private boolean headache;

    @TableField("nosebleed")
    @ApiModelProperty(value = "是否流鼻血", example = "0")
    private boolean nosebleed;

    @TableField("nausea")
    @ApiModelProperty(value = "是否恶心", example = "0")
    private boolean nausea;

    @TableField("vomiting_count")
    @ApiModelProperty(value = "呕吐次数", example = "0")
    private int vomitingCount;

    @TableField("abdominal_pain")
    @ApiModelProperty(value = "是否腹痛", example = "0")
    private boolean abdominalPain;

    @TableField("diarrhea_count")
    @ApiModelProperty(value = "腹泻次数", example = "0")
    private int diarrheaCount;
}
