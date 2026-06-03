package com.xiaotiyun.school.manager.model.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class GradeYearExportEnModel {

    @ExcelProperty("Subject Name")
    private String subjectName;

    @ExcelProperty("Qualified Count")
    private Integer qualifiedCount;

    @ExcelProperty("Qualified Rate")
    private String qualifiedRate;

    @ExcelProperty("Flunk Count")
    private Integer flunkCount;

    @ExcelProperty("Flunk Rate")
    private String flunkRate;

    @ExcelProperty("60-80 Count")
    private Integer sixtyToEightyCount;

    @ExcelProperty("60-80 Rate")
    private String sixtyToEightyRate;

    @ExcelProperty("80-90 Count")
    private Integer eightyToNinetyCount;

    @ExcelProperty("80-90 Rate")
    private String eightyToNinetyRate;

    @ExcelProperty("90-100 Count")
    private Integer ninetyToHundredCount;

    @ExcelProperty("90-100 Rate")
    private String ninetyToHundredRate;
}
