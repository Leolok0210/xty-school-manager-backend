package com.xiaotiyun.school.manager.model.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class ConventionalPerformanceImportModel extends BasicImportModel {
    @ExcelProperty("学生姓名")
    private String studentName;
    @ExcelProperty("学生编号")
    private String studentCode;
    @ExcelProperty("事件日期")
    private String date;
    @ExcelProperty("欠作业")
    private String missingHomework;
    @ExcelProperty("欠课本")
    private String missingTextbook;
    @ExcelProperty("上课违规")
    private String classViolation;
    @ExcelProperty("仪表不符")
    private String uniformNonCompliance;
    @ExcelProperty("欠回条")
    private String missingReturnSticker;
    @ExcelProperty(value = "欠作業", index = 8)
    private String missingHomeworkRemark;
    @ExcelProperty(value = "欠課本", index = 9)
    private String missingTextbookRemark;
    @ExcelProperty(value = "上課違規", index = 10)
    private String classViolationRemark;
    @ExcelProperty(value = "儀表不符", index = 11)
    private String uniformNonComplianceRemark;
    @ExcelProperty(value = "欠回條", index = 12)
    private String missingReturnStickerRemark;
}