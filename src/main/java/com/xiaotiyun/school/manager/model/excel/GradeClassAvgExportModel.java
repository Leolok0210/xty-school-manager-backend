package com.xiaotiyun.school.manager.model.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class GradeClassAvgExportModel {
    @ExcelProperty("班級名稱")
    private String className;

    @ExcelProperty("平均分")
    private String averageScore;

    @ExcelProperty("班級人數")
    private Integer classSize;
}
