package com.xiaotiyun.school.manager.model.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class TeacherAttendanceImportZhTwModel extends BasicImportModel {
    @ExcelProperty(value = "教師用戶編號（必填）", index = 0)
    private String userNumber;
    @ExcelProperty(value = "教師姓名（必填）", index = 1)
    private String teacherName;
    @ExcelProperty(value = "卡號", index = 2)
    private String cardNumber;
    @ExcelProperty(value = "日期（必填）", index = 3)
    private String attendanceDate;
    @ExcelProperty(value = "時間（必填）", index = 4)
    private String attendanceTime;
}