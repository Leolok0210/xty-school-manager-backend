package com.xiaotiyun.school.manager.model.excel;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * 活动学生报告导出模型（葡萄牙语）
 */
@Data
@ExcelIgnoreUnannotated
public class ActivityStudentReportExportPtModel {
    
    @ExcelProperty("Curso")
    private String courseName;
    
    @ExcelProperty("Nome")
    private String studentName;
    
    @ExcelProperty("Turma")
    private String className;
    
    @ExcelProperty("Número do Estudante")
    private String studentNo;
} 