package com.xiaotiyun.school.manager.model.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class ConventionalPerformanceEnExportModel {
    @ExcelProperty("Term")
    private String termName;
    @ExcelProperty("Event Date")
    private String date;
    @ExcelProperty("Class Name")
    private String className;
    @ExcelProperty("Student Name")
    private String studentName;
    @ExcelProperty("Type")
    private String type;
    @ExcelProperty("Frequency")
    private Integer frequency;
    @ExcelProperty("Create Time")
    private String createTime;
    @ExcelProperty("Creator")
    private String createName;
}