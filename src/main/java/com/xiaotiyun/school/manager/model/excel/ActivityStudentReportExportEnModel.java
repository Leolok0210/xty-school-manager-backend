package com.xiaotiyun.school.manager.model.excel;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * 活动学生报告导出模型（英文）
 */
@Data
@ExcelIgnoreUnannotated
public class ActivityStudentReportExportEnModel {
    
    @ExcelProperty("Course")
    private String courseName;
    
    @ExcelProperty("Name")
    private String studentName;
    
    @ExcelProperty("Class")
    private String className;
    
    @ExcelProperty("Student No")
    private String studentNo;
} 