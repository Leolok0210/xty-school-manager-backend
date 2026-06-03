package com.xiaotiyun.school.manager.model.excel;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
@ExcelIgnoreUnannotated
public class ImageRecordExportPtModel {
    @ExcelProperty("Nome do Arquivo")
    private String incorrectLineno;
    
    @ExcelProperty("Motivo do Erro")
    private String incorrectReason;
} 