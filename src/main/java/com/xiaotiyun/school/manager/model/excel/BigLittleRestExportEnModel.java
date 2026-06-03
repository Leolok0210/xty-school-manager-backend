package com.xiaotiyun.school.manager.model.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class BigLittleRestExportEnModel {
    @ExcelProperty("School year")
    private String schoolYear;

    @ExcelProperty("Section name")
    private String semesterName;

    @ExcelProperty("Class name")
    private String className;

    @ExcelProperty("Student name")
    private String studentName;

    @ExcelProperty("Date")
    private String registrationDate;

    @ExcelProperty("Big/small breath")
    private String type;

    @ExcelProperty("Big/small breath performance")
    private String registrationContent;

    @ExcelProperty("Registrant")
    private String registrant;
}
