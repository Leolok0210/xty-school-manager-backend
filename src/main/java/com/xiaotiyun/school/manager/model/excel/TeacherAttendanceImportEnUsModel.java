package com.xiaotiyun.school.manager.model.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class TeacherAttendanceImportEnUsModel extends BasicImportModel {
    @ExcelProperty(value = "Teacher User ID (required)", index = 0)
    private String userNumber;
    @ExcelProperty(value = "Teacher's Name (required)", index = 1)
    private String teacherName;
    @ExcelProperty(value = "Card Number", index = 2)
    private String cardNumber;
    @ExcelProperty(value = "Date (required)", index = 3)
    private String attendanceDate;
    @ExcelProperty(value = "Time (required)", index = 4)
    private String attendanceTime;
} 