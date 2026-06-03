package com.xiaotiyun.school.manager.model.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class StudentMedicalRecordImportEnModel {
    /**
     * 學生中文姓名（必填）
     */
    @ExcelProperty("Student Chinese Name (Mandatory)")
    private String studentName;

    /**
     * 學生編號（必填）
     */
    @ExcelProperty("Student ID (Mandatory)")
    private String studentNumber;

    /**
     * 求诊时间
     */
    @ExcelProperty("Visit Date (Mandatory)")
    private String consultationDate;

    /**
     * 求诊时间
     */
    @ExcelProperty("Visit Time (Mandatory)")
    private String consultationTime;

    /**
     * 处理，必填字段
     */
    @ExcelProperty("Treatment (Mandatory)")
    private String treatment;

    /**
     * 备注，必填字段
     */
    @ExcelProperty("Remarks (Mandatory)")
    private String notes;

    /**
     * 体温
     */
    @ExcelProperty("Temperature")
    private String temperature;

    /**
     * 是否发热
     */
    @ExcelProperty("Fever")
    private String fever;

    /**
     * 是否咳嗽
     */
    @ExcelProperty("Cough")
    private String cough;

    /**
     * 是否流涕
     */
    @ExcelProperty("Runny Nose")
    private String runnyNose;

    /**
     * 是否咽痛
     */
    @ExcelProperty("Sore Throat")
    private String soreThroat;

    /**
     * 是否头晕
     */
    @ExcelProperty("Dizziness")
    private String dizziness;

    /**
     * 是否头痛
     */
    @ExcelProperty("Headache")
    private String headache;

    /**
     * 是否流鼻血
     */
    @ExcelProperty("Nosebleed")
    private String nosebleed;

    /**
     * 是否恶心
     */
    @ExcelProperty("Nausea")
    private String nausea;

    /**
     * 呕吐次数
     */
    @ExcelProperty("Vomiting Frequency")
    private String vomitingCount;

    /**
     * 是否腹痛
     */
    @ExcelProperty("Abdominal Pain")
    private String abdominalPain;

    /**
     * 腹泻次数
     */
    @ExcelProperty("Diarrhea Frequency")
    private String diarrheaCount;

    /**
     * 主诉现病史
     */
    @ExcelProperty("Other Symptoms")
    private String chiefComplaint;

    private Integer excelLineNo;
}
