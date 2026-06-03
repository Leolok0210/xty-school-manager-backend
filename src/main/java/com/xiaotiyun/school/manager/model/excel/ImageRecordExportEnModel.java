package com.xiaotiyun.school.manager.model.excel;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
@ExcelIgnoreUnannotated
public class ImageRecordExportEnModel {
    @ExcelProperty("File Name")
    private String incorrectLineno;
    
    @ExcelProperty("Error Reason")
    private String incorrectReason;
} 