package com.xiaotiyun.school.manager.model.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class TeacherAttendanceExportModel {
    @ExcelProperty("日期")
    private String attendanceDate;
    @ExcelProperty("教師編號")
    private String teacherNumber;
    @ExcelProperty("教師姓名")
    private String teacherName;
    @ExcelProperty("卡號")
    private String cardNumber;
    @ExcelProperty("上班時間")
    private String clockInTime;
    @ExcelProperty("下班時間")
    private String clockOutTime;
    @ExcelProperty("狀態")
    private String status;
    @ExcelProperty("備註")
    private String remark;
} 