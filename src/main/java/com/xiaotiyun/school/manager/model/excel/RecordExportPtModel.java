package com.xiaotiyun.school.manager.model.excel;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
@ExcelIgnoreUnannotated
public class RecordExportPtModel {
    @ExcelProperty("Número da Linha")
    private String incorrectLineno;
    
    @ExcelProperty("Motivo do Erro")
    private String incorrectReason;
} 