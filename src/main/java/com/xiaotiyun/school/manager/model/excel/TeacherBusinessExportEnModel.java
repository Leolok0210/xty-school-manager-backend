package com.xiaotiyun.school.manager.model.excel;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
@ExcelIgnoreUnannotated
public class TeacherBusinessExportEnModel {
    @ExcelProperty("Teacher Name")
    private String teacherName;
    @ExcelProperty("Start Date & Time")
    private String startTime;
    @ExcelProperty("End Date & Time")
    private String endTime;
    @ExcelProperty("Reason")
    private String reason;
}
