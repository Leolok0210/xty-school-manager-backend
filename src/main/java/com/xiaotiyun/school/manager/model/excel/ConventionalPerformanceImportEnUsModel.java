package com.xiaotiyun.school.manager.model.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class ConventionalPerformanceImportEnUsModel extends BasicImportModel {
    @ExcelProperty(value = "Student Chinese Name(required)", index = 0)
    private String studentName;
    @ExcelProperty(value = "Student ID(required)", index = 1)
    private String studentCode;
    @ExcelProperty(value = "Incident Date(required)", index = 2)
    private String date;
    @ExcelProperty(value = "Missing Homework(required)\n" +
            "Enter 0 if no violation", index = 3)
    private String missingHomework;
    @ExcelProperty(value = "Missing Textbook(required)\n" +
            "Enter 0 if no violation", index = 4)
    private String missingTextbook;
    @ExcelProperty(value = "Classroom Violation(required)\n" +
            "Enter 0 if no violation", index = 5)
    private String classViolation;
    @ExcelProperty(value = "Improper Grooming(required)\n" +
            "Enter 0 if no violation", index = 6)
    private String uniformNonCompliance;
    @ExcelProperty(value = "Missing Return Slip(required)\n" +
            "Enter 0 if no violation", index = 7)
    private String missingReturnSticker;
    @ExcelProperty(value = "Missing Homework", index = 8)
    private String missingHomeworkRemark;
    @ExcelProperty(value = "Missing Textbook", index = 9)
    private String missingTextbookRemark;
    @ExcelProperty(value = "Classroom Violation", index = 10)
    private String classViolationRemark;
    @ExcelProperty(value = "Improper Grooming", index = 11)
    private String uniformNonComplianceRemark;
    @ExcelProperty(value = "Missing Return Slip", index = 12)
    private String missingReturnStickerRemark;
}