package com.xiaotiyun.school.manager.model.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class TeacherLeaveExportModel {
    @ExcelProperty("請假老師")
    private String teacherName;
    @ExcelProperty("請假類型")
    private String leaveType;
    @ExcelProperty("開始日期")
    private String startTime;
    @ExcelProperty("結束時間")
    private String endTime;
    @ExcelProperty("請假事由")
    private String reason;
} 