package com.xiaotiyun.school.manager.model.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class RecordExportModel {
    @ExcelProperty("行号")
    private String incorrectLineno;
    @ExcelProperty("失败原因")
    private String incorrectReason;
}