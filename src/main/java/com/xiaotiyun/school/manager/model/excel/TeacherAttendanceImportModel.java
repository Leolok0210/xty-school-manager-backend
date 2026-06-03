package com.xiaotiyun.school.manager.model.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class TeacherAttendanceImportModel extends BasicImportModel {
    @ExcelProperty("用户编号")
    private String userNumber;
    @ExcelProperty("教师姓名")
    private String teacherName;
    @ExcelProperty("卡号")
    private String cardNumber;
    @ExcelProperty("日期")
    private String attendanceDate;
    @ExcelProperty("时间")
    private String attendanceTime;
}