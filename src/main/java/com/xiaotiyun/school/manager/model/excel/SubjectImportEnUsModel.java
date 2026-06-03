package com.xiaotiyun.school.manager.model.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class SubjectImportEnUsModel {
    @ExcelProperty("Subject Code (required)")
    private String subjectNumber;

    @ExcelProperty("Subject Name (required)")
    private String subjectName;

    @ExcelProperty("English Name")
    private String subjectEnglishName;

    @ExcelProperty("Unit \n" +
            "(Format: number, 1-100)")
    private String unit;
    @ExcelProperty("Applicable Department (required)\n" +
            "Supported inputs: Kindergarten, Primary School, Secondary School \n" +
            "For multiple values: separate with comma \",\"")
    private String scope;

    private Integer rowIndex;
}