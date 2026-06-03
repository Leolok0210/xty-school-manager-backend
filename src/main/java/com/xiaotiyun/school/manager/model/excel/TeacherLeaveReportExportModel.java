package com.xiaotiyun.school.manager.model.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class TeacherLeaveReportExportModel {
    @ExcelProperty("名稱")
    private String teacherName;

    @ExcelProperty("職務")
    private String teacherPosition;

    @ExcelProperty("編號")
    private String teacherNumber;

    @ExcelProperty("應出勤天數")
    private Integer shouldAttendanceDays;

    @ExcelProperty("實際出勤天數")
    private Integer actualAttendanceDays;

    @ExcelProperty("遲到次數")
    private Integer lateCount;

    @ExcelProperty("早退次數")
    private Integer earlyCount;

    @ExcelProperty("請假次數")
    private Integer leaveCount;

    @ExcelProperty("公務次數")
    private Integer officialCount;

    @ExcelProperty("無由不打卡次數")
    private Integer noReasonCount;
}
