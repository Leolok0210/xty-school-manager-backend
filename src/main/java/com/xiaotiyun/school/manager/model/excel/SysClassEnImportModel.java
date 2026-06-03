package com.xiaotiyun.school.manager.model.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class SysClassEnImportModel {

    private Integer rowIndex;

    @ExcelProperty("Grade Group (required，Select from dropdown)")
    private String gradeGroup;

    @ExcelProperty("Class Name")
    private String className;

    @ExcelProperty("Class Number (required)")
    private String classSerialNumber;

//    @ExcelProperty("Is Specialized Class (required)")
//    private String professionalVersion;

    @ExcelProperty("Stream（Select from dropdown)")
    private String artsScience;

    @ExcelProperty("Major Name")
    private String professional;

    @ExcelProperty("Homeroom Teacher's User ID(required)")
    private String headTeacher;



}