package com.xiaotiyun.school.manager.model.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * 活动学生报告导出模型（中文）
 */
@Data
public class ActivityStudentReportExportModel {
    
    @ExcelProperty("課程")
    private String courseName;
    
    @ExcelProperty("姓名")
    private String studentName;
    
    @ExcelProperty("班級")
    private String className;
    
    @ExcelProperty("學號")
    private String studentNo;
} 