package com.xiaotiyun.school.manager.model.req;

import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class StudentMedicalRecordAddReqModel {
    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty(value = "学年", example = "2023-2024", required = true)
    private String schoolYear;

    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty(value = "班级ID", example = "1", required = true)
    private Long classId;

    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty(value = "学生ID", example = "12345", required = true)
    private Long studentId;

    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty(value = "学生姓名", example = "张三", required = true)
    private String studentName;

    @ApiModelProperty(value = "主诉现病史", example = "其他原因")
    private String chiefComplaint;

    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty(value = "处理", example = "服用退烧药", required = true)
    private String treatment;

    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty(value = "备注", example = "按时复查", required = true)
    private String notes;

    @ApiModelProperty(value = "求诊时间", example = "2023-10-01 10:00:00")
    private String consultationDate;

    @ApiModelProperty(value = "体温", example = "36.5")
    private String temperature;

    @ApiModelProperty(value = "是否发热", example = "0")
    private boolean fever;

    @ApiModelProperty(value = "是否咳嗽", example = "0")
    private boolean cough;

    @ApiModelProperty(value = "是否流涕", example = "0")
    private boolean runnyNose;

    @ApiModelProperty(value = "是否咽痛", example = "0")
    private boolean soreThroat;

    @ApiModelProperty(value = "是否头晕", example = "0")
    private boolean dizziness;

    @ApiModelProperty(value = "是否头痛", example = "0")
    private boolean headache;

    @ApiModelProperty(value = "是否流鼻血", example = "0")
    private boolean nosebleed;

    @ApiModelProperty(value = "是否恶心", example = "0")
    private boolean nausea;

    @ApiModelProperty(value = "呕吐次数", example = "0")
    private int vomitingCount;

    @ApiModelProperty(value = "是否腹痛", example = "0")
    private boolean abdominalPain;

    @ApiModelProperty(value = "腹泻次数", example = "0")
    private int diarrheaCount;
}
