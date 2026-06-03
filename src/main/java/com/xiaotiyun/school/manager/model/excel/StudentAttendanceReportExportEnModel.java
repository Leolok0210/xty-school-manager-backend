package com.xiaotiyun.school.manager.model.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class StudentAttendanceReportExportEnModel {
    @ExcelProperty("Class Number")
    private String seatNo;
    @ExcelProperty("Student Name")
    private String studentName;
    @ExcelProperty("Expected Clock-in Days")
    private String clockDays;
    @ExcelProperty("Actual Clock-in Days")
    private String actualClockDays;
    @ExcelProperty("Late Days")
    private String beLateDays;
    @ExcelProperty("Early Departure Days")
    private String earlyDays;
    @ExcelProperty("Leave Days")
    private String leaveDays;
    @ExcelProperty("Official Days")
    private String businessDays;
    @ExcelProperty("Unexcused Absence Days")
    private String notClockDays;
} 