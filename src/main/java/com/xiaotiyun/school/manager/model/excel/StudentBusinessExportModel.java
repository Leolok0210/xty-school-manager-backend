package com.xiaotiyun.school.manager.model.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class StudentBusinessExportModel {
    @ExcelProperty("學年")
    private String schoolYear;
    @ExcelProperty("班級名稱")
    private String className;
    @ExcelProperty("學生姓名")
    private String studentName;
    @ExcelProperty("開始日期")
    private String startTime;
    @ExcelProperty("結束日期")
    private String endTime;
    @ExcelProperty("事由")
    private String reason;
} 