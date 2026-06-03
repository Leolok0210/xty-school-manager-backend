package com.xiaotiyun.school.manager.model.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class StudentAttendanceReportExportModel {
    @ExcelProperty("班內號")
    private String seatNo;
    @ExcelProperty("學生姓名")
    private String studentName;
    @ExcelProperty("應到校打卡天數")
    private String clockDays;
    @ExcelProperty("實際到校打卡天數")
    private String actualClockDays;
    @ExcelProperty("遲到次數")
    private String beLateDays;
    @ExcelProperty("早退次數")
    private String earlyDays;
    @ExcelProperty("請假次數")
    private String leaveDays;
    @ExcelProperty("公務天數")
    private String businessDays;
    @ExcelProperty("無由不打卡天數")
    private String notClockDays;
}