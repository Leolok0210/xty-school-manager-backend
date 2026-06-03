package com.xiaotiyun.school.manager.model.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class MedicalRecordExportEnModel {

    @ExcelProperty("Class")
    private String className;

    @ExcelProperty("Student Name")
    private String studentName;

    @ExcelProperty("Gender")
    private String genderStr;

    @ExcelProperty("Allergy")
    private String allergy;

    @ExcelProperty("Serious Chronic Disease")
    private String seriousChronicDisease;

    @ExcelProperty("Medical Notes")
    private String medicalNotes;

    @ExcelProperty("Hospital")
    private String hospital;

    @ExcelProperty("Chief Complaint")
    private String chiefComplaintAll;

    @ExcelProperty("Treatment")
    private String treatment;

    @ExcelProperty("Notes")
    private String notes;

    @ExcelProperty("Consultation Date")
    private String consultationDate;
}
