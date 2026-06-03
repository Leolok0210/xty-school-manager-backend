package com.xiaotiyun.school.manager.model.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class GradeYearExportModel {

    @ExcelProperty("科目名稱")
    private String subjectName;

    @ExcelProperty("合格人數")
    private Integer qualifiedCount;

    @ExcelProperty("合格率")
    private String qualifiedRate;

    @ExcelProperty("不合格人數")
    private Integer flunkCount;

    @ExcelProperty("不合格率")
    private String flunkRate;

    @ExcelProperty("60-80人數")
    private Integer sixtyToEightyCount;

    @ExcelProperty("60-80率")
    private String sixtyToEightyRate;

    @ExcelProperty("80-90人數")
    private Integer eightyToNinetyCount;

    @ExcelProperty("80-90率")
    private String eightyToNinetyRate;

    @ExcelProperty("90-100人數")
    private Integer ninetyToHundredCount;

    @ExcelProperty("90-100率")
    private String ninetyToHundredRate;
}
