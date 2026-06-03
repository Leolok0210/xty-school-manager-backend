package com.xiaotiyun.school.manager.model.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode
public class ConventionalPerformanceExportModel {
    @ExcelProperty("學段")
    private String termName;
    @ExcelProperty("事件日期")
    private String date;
    @ExcelProperty("班級名稱")
    private String className;
    @ExcelProperty("學生姓名")
    private String studentName;
    @ExcelProperty("類型")
    private String type;
    @ExcelProperty("次數")
    private Integer frequency;
    @ExcelProperty("創建時間")
    private String createTime;
    @ExcelProperty("創建人")
    private String createName;
}