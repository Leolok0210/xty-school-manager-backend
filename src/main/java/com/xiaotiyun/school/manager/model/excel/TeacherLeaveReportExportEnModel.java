package com.xiaotiyun.school.manager.model.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class TeacherLeaveReportExportEnModel {
    @ExcelProperty("Name")
    private String teacherName;

    @ExcelProperty("Position")
    private String teacherPosition;

    @ExcelProperty("Number")
    private String teacherNumber;

    @ExcelProperty("Required Attendance Days")
    private Integer shouldAttendanceDays;

    @ExcelProperty("Actual Attendance Days")
    private Integer actualAttendanceDays;

    @ExcelProperty("Late Count")
    private Integer lateCount;

    @ExcelProperty("Early Departure Count")
    private Integer earlyCount;

    @ExcelProperty("Leave Count")
    private Integer leaveCount;

    @ExcelProperty("Official Business Count")
    private Integer officialCount;

    @ExcelProperty("No Reason Count")
    private Integer noReasonCount;
}
