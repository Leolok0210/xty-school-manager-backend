package com.xiaotiyun.school.manager.model.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class StudentExportEnModel {
    @ExcelProperty("School Year") // 學年
    private String schoolYear;
    @ExcelProperty("Student Name") // 學生姓名
    private String studentName;
    @ExcelProperty("Gender") // 性別
    private String gender;
    @ExcelProperty("Student Number") // 學生編號
    private String studentNo;
    @ExcelProperty("Seat Number") // 座位號
    private String seatNo;
    @ExcelProperty("Class Name") // 班級名稱
    private String className;
    @ExcelProperty("Grade Group") // 級組
    private String gradeName;
    @ExcelProperty("Status") // 狀態
    private String status;
    @ExcelProperty("English Name") // 英文名
    private String englishName;
    @ExcelProperty("Education Bureau Number") // 教青局編號
    private String educationNo;
    @ExcelProperty("Nationality") // 國籍
    private String nationality;
    @ExcelProperty("Native Place") // 籍貫
    private String nativePlace;
    @ExcelProperty("ID Type") // 證件類型
    private String idType;
    @ExcelProperty("ID Number") // 證件編號
    private String idNo;
    @ExcelProperty("Permanent Address") // 常住地址
    private String permanentAddress;
    @ExcelProperty("Mobile Phone") // 手提電話
    private String mobilePhone;
    @ExcelProperty("Permanent Address Phone") // 住址電話
    private String permanentPhone;
    @ExcelProperty("Dropout Date") // 退学日期
    private String outTime;
    @ExcelProperty("Dropout Reason") // 退学原因
    private String outReason;
    @ExcelProperty("Promotion/Retention Status") // 升留级情况
    private String escalationSituation;
} 