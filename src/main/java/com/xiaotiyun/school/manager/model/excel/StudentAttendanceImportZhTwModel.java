package com.xiaotiyun.school.manager.model.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class StudentAttendanceImportZhTwModel extends BasicImportModel {
    @ExcelProperty(value = "學生編號（必填）", index = 0)
    private String studentNo;
    @ExcelProperty(value = "學生姓名（必填）", index = 1)
    private String studentName;
    @ExcelProperty(value = "類型（必填）", index = 2)
    private String type;
    @ExcelProperty(value = "日期（必填）", index = 3)
    private String attendanceDate;
    @ExcelProperty(value = "時間（必填）", index = 4)
    private String attendanceTime;
}