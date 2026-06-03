package com.xiaotiyun.school.manager.model.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class StudentLeaveExportModel {
    @ExcelProperty("學年")
    private String schoolYear;
    @ExcelProperty("班級名稱")
    private String className;
    @ExcelProperty("班內號")
    private String seatNo;
    @ExcelProperty("學生名稱")
    private String studentName;
    @ExcelProperty("日期")
    private String leaveDate;
    @ExcelProperty("類型")
    private String leaveType;
    @ExcelProperty("節數")
    private String periods;
    @ExcelProperty("備註")
    private String remark;
}