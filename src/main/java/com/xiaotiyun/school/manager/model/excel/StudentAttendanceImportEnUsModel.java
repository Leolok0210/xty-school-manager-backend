package com.xiaotiyun.school.manager.model.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class StudentAttendanceImportEnUsModel extends BasicImportModel {
    @ExcelProperty(value = "Student ID (required)", index = 0)
    private String studentNo;
    @ExcelProperty(value = "Chinese Name (required)", index = 1)
    private String studentName;
    @ExcelProperty(value = "Type (required)", index = 2)
    private String type;
    @ExcelProperty(value = "Date (required)", index = 3)
    private String attendanceDate;
    @ExcelProperty(value = "Time (required)", index = 4)
    private String attendanceTime;
} 