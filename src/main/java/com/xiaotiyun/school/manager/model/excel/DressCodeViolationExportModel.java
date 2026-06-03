package com.xiaotiyun.school.manager.model.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class DressCodeViolationExportModel {
    @ExcelProperty("学年")
    private String schoolYear;

    @ExcelProperty("学段名称")
    private String semesterName;

    @ExcelProperty("班级名称")
    private String className;

    @ExcelProperty("班内号")
    private String studentClassNumber;

    @ExcelProperty("学生姓名")
    private String studentName;

    @ExcelProperty("日期")
    private String violationDate;

    @ExcelProperty("备注")
    private String remark;

    @ExcelProperty("登记人")
    private String registrant;
}
