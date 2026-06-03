package com.xiaotiyun.school.manager.model.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class StudentAttendanceImportModel extends BasicImportModel {
    @ExcelProperty("学生编号")
    private String studentNo;
    @ExcelProperty("学生姓名")
    private String studentName;
    @ExcelProperty("类型")
    private String type;
    @ExcelProperty("日期")
    private String attendanceDate;
    @ExcelProperty("时间")
    private String attendanceTime;
}