package com.xiaotiyun.school.manager.model.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class ConventionalPerformanceImportZhTwModel extends BasicImportModel {
    @ExcelProperty(value = "學生中文姓名（必填）", index = 0)
    private String studentName;
    @ExcelProperty(value = "學生編號（必填）", index = 1)
    private String studentCode;
    @ExcelProperty(value = "事件日期（必填）", index = 2)
    private String date;
    @ExcelProperty(value = "欠作業（必填）\n" +
            "無該違規請輸入0", index = 3)
    private String missingHomework;
    @ExcelProperty(value = "欠課本（必填）\n" +
            "無該違規請輸入0", index = 4)
    private String missingTextbook;
    @ExcelProperty(value = "上课违规（必填）\n" +
            "無該違規請輸入0", index = 5)
    private String classViolation;
    @ExcelProperty(value = "儀表不符（必填）\n" +
            "無該違規請輸入0", index = 6)
    private String uniformNonCompliance;
    @ExcelProperty(value = "欠回条（必填）\n" +
            "無該違規請輸入0", index = 7)
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