package com.xiaotiyun.school.manager.model.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class PatrolRegistrationExportModel {
    @ExcelProperty("学年")
    private String schoolYear;

    @ExcelProperty("学段")
    private String semesterName;

    @ExcelProperty("班級名称")
    private String className;

    @ExcelProperty("班内号")
    private Long studentClassNumber;

    @ExcelProperty("學生姓名")
    private String studentName;

    @ExcelProperty("日期")
    private String registrationDate;

    @ExcelProperty("課節")
    private String lessonPeriod;

    @ExcelProperty("登記內容明細")
    private String registrationContent;

    @ExcelProperty("登記人")
    private String registrant;
}
