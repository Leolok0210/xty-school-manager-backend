package com.xiaotiyun.school.manager.model.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class BigLittleRestExportModel {
    @ExcelProperty("学年")
    private String schoolYear;

    @ExcelProperty("学段名称")
    private String semesterName;

    @ExcelProperty("班級名稱")
    private String className;

    @ExcelProperty("学生姓名")
    private String studentName;

    @ExcelProperty("日期")
    private String registrationDate;

    @ExcelProperty("大息/小息")
    private String type;

    @ExcelProperty("大息小息表現")
    private String registrationContent;

    @ExcelProperty("登記人")
    private String registrant;
}
