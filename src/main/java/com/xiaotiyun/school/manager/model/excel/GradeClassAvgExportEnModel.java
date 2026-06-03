package com.xiaotiyun.school.manager.model.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class GradeClassAvgExportEnModel {
    @ExcelProperty("Class name")
    private String className;

    @ExcelProperty("Average score")
    private String averageScore;

    @ExcelProperty("Class size")
    private Integer classSize;
}
