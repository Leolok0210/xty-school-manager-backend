package com.xiaotiyun.school.manager.model.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class StudentAttendanceExportModel {
    @ExcelProperty("日期")
    private String attendanceDate;
    @ExcelProperty("學生編號")
    private String studentNo;
    @ExcelProperty("班級")
    private String className;
    @ExcelProperty("學生姓名")
    private String studentName;
    @ExcelProperty("入校時間（上午）")
    private String morningInTime;
    @ExcelProperty("離校時間（上午）")
    private String morningOutTime;
    @ExcelProperty("入校時間（下午）")
    private String afternoonInTime;
    @ExcelProperty("離校時間（下午）")
    private String afternoonOutTime;
    @ExcelProperty("狀態")
    private String status;
    @ExcelProperty("備註")
    private String remark;
} 