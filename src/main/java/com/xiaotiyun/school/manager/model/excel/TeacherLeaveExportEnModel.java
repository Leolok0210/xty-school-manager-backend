package com.xiaotiyun.school.manager.model.excel;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
@ExcelIgnoreUnannotated
public class TeacherLeaveExportEnModel {
    @ExcelProperty("Teacher on Leave")
    private String teacherName;
    @ExcelProperty("Leave Type")
    private String leaveType;
    @ExcelProperty("Start Date & Time")
    private String startTime;
    @ExcelProperty("End Date & Time")
    private String endTime;
    @ExcelProperty("Reason for Leave")
    private String reason;
}
