package com.xiaotiyun.school.manager.model.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class PatrolRegistrationExportEnModel {
    @ExcelProperty("School year")
    private String schoolYear;

    @ExcelProperty("Period of study")
    private String semesterName;

    @ExcelProperty("Class name")
    private String className;

    @ExcelProperty("Class number")
    private Long studentClassNumber;

    @ExcelProperty("Student name")
    private String studentName;

    @ExcelProperty("Date")
    private String registrationDate;

    @ExcelProperty("Class period")
    private String lessonPeriod;

    @ExcelProperty("Registration details")
    private String registrationContent;

    @ExcelProperty("Registrant")
    private String registrant;
}
