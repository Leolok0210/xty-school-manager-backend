package com.xiaotiyun.school.manager.model.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * 活动匹配导入模型（英文）
 */
@Data
public class ActivityStudentReportImportEnModel {
    @ExcelProperty("Student（required）")
    private String studentName;

    @ExcelProperty("Student code（required）")
    private String studentNo;

    /**
     * Excel行号
     */
    private Integer rowIndex;
} 