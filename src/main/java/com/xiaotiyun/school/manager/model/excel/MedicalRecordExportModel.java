package com.xiaotiyun.school.manager.model.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class MedicalRecordExportModel {

    @ExcelProperty("班級")
    private String className;

    @ExcelProperty("學生姓名")
    private String studentName;

    @ExcelProperty("性別")
    private String genderStr;

    @ExcelProperty("過敏")
    private String allergy;

    @ExcelProperty("嚴重/慢性疾病")
    private String seriousChronicDisease;

    @ExcelProperty("醫護備註")
    private String medicalNotes;

    @ExcelProperty("如遇意外應送往之醫院")
    private String hospital;

    @ExcelProperty("主訴現病史")
    private String chiefComplaintAll;

    @ExcelProperty("處理")
    private String treatment;

    @ExcelProperty("備註")
    private String notes;

    @ExcelProperty("求診時間")
    private String consultationDate;
}
