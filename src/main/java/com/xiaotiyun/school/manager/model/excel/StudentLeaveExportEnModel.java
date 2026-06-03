package com.xiaotiyun.school.manager.model.excel;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
@ExcelIgnoreUnannotated
public class StudentLeaveExportEnModel {
    @ExcelProperty("Academic Year")
    private String schoolYear;
    @ExcelProperty("Class Name")
    private String className;
    @ExcelProperty("Seat Number")
    private String seatNo;
    @ExcelProperty("Student Name")
    private String studentName;
    @ExcelProperty("Date")
    private String leaveDate;
    @ExcelProperty("Type")
    private String leaveType;
    @ExcelProperty("Number of Classes")
    private String periods;
    @ExcelProperty("Remark")
    private String remark;
}
