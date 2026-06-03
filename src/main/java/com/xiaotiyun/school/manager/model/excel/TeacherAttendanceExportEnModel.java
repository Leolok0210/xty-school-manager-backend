package com.xiaotiyun.school.manager.model.excel;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
@ExcelIgnoreUnannotated
public class TeacherAttendanceExportEnModel {
    @ExcelProperty("Date")
    private String attendanceDate;
    @ExcelProperty("Teacher ID")
    private String teacherNumber;
    @ExcelProperty("Teacher Name")
    private String teacherName;
    @ExcelProperty("Card Number")
    private String cardNumber;
    @ExcelProperty("Check-in Time")
    private String clockInTime;
    @ExcelProperty("Check-out Time")
    private String clockOutTime;
    @ExcelProperty("Status")
    private String status;
    @ExcelProperty("Remark")
    private String remark;
}
