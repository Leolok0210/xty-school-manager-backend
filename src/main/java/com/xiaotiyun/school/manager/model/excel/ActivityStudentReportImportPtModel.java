package com.xiaotiyun.school.manager.model.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * 活动匹配导入模型（葡萄牙语）
 */
@Data
public class ActivityStudentReportImportPtModel {
    @ExcelProperty("Nome do Aluno (Obrigatório)")
    private String studentName;

    @ExcelProperty("Número do Aluno (Obrigatório)")
    private String studentNo;

    /**
     * Excel行号
     */
    private Integer rowIndex;
} 