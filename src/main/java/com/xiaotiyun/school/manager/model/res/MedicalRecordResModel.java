package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "医护保健记录返回对象")
public class MedicalRecordResModel {
    @ApiModelProperty(value = "主键ID", example = "1")
    private Long id;

    @ApiModelProperty(value = "学年", example = "2023-2024")
    private String schoolYear;

    @ApiModelProperty(value = "级组ID", example = "1")
    private Long classGroupId;

    @ApiModelProperty(value = "级组名称", example = "高一")
    private String classGroupName;

    @ApiModelProperty(value = "班级ID", example = "1")
    private Long classId;

    @ApiModelProperty(value = "班级名称", example = "高一(1)班")
    private String className;

    @ApiModelProperty(value = "学生ID", example = "12345")
    private Long studentId;

    @ApiModelProperty(value = "学生姓名", example = "张三")
    private String studentName;

    @ApiModelProperty(value = "性别", example = "男")
    private Long gender;

    @ApiModelProperty(value = "过敏", example = "青霉素")
    private String allergy;

    @ApiModelProperty(value = "严重/慢性疾病", example = "哮喘")
    private String seriousChronicDisease;

    @ApiModelProperty(value = "医护备注", example = "注意休息")
    private String medicalNotes;

    @ApiModelProperty(value = "如遇意外应送往之医院", example = "市人民医院")
    private String hospital;

    @ApiModelProperty(value = "其他", example = "其他原因")
    private String chiefComplaint;

    @ApiModelProperty(value = "主诉现病史", example = "其他原因")
    private String chiefComplaintAll;

    @ApiModelProperty(value = "处理", example = "服用退烧药")
    private String treatment;

    @ApiModelProperty(value = "备注", example = "按时复查")
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