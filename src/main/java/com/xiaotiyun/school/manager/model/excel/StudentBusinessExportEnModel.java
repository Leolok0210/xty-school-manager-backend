package com.xiaotiyun.school.manager.model.excel;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
@ExcelIgnoreUnannotated
public class StudentBusinessExportEnModel {
    @ExcelProperty("Academic Year")
    private String schoolYear;
    @ExcelProperty("Class Name")
    private String className;
    @ExcelProperty("Student Name")
    private String studentName;
    @ExcelProperty("Start Date")
    private String startTime;
    @ExcelProperty("End Date")
    private String endTime;
    @ExcelProperty("Reason")
    private String reason;
}
