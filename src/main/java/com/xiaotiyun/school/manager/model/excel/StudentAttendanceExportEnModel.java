package com.xiaotiyun.school.manager.model.excel;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
@ExcelIgnoreUnannotated
public class StudentAttendanceExportEnModel {
    @ExcelProperty("Date")
    private String attendanceDate;
    @ExcelProperty("Student ID")
    private String studentNo;
    @ExcelProperty("Class")
    private String className;
    @ExcelProperty("Student Name")
    private String studentName;
    @ExcelProperty("Check-in Time (Morning)")
    private String morningInTime;
    @ExcelProperty("Check-out Time (Morning)")
    private String morningOutTime;
    @ExcelProperty("Check-in Time (Afternoon)")
    private String afternoonInTime;
    @ExcelProperty("Check-out Time (Afternoon)")
    private String afternoonOutTime;
    @ExcelProperty("Status")
    private String status;
    @ExcelProperty("Remark")
    private String remark;
}
