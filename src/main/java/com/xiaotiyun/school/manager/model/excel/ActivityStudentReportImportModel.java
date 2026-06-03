package com.xiaotiyun.school.manager.model.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * 活动匹配导入模型
 */
@Data
public class ActivityStudentReportImportModel {
    
    /**
     * 行号
     */
    private Integer rowIndex;
    
    /**
     * 学生姓名
     */
    @ExcelProperty(value = "學生（必填）")
    private String studentName;
    
    /**
     * 学生编号
     */
    @ExcelProperty(value = "學生編號（必填）")
    private String studentNo;
} 