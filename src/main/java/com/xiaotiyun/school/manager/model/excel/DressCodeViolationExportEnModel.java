package com.xiaotiyun.school.manager.model.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class DressCodeViolationExportEnModel {
    @ExcelProperty("School year")
    private String schoolYear;

    @ExcelProperty("Section name")
    private String semesterName;

    @ExcelProperty("Class name")
    private String className;

    @ExcelProperty("Class number")
    private String studentClassNumber;

    @ExcelProperty("Student name")
    private String studentName;

    @ExcelProperty("Date")
    private String violationDate;

    @ExcelProperty("Remark")
    private String remark;

    @ExcelProperty("Registrant")
    private String registrant;
}
