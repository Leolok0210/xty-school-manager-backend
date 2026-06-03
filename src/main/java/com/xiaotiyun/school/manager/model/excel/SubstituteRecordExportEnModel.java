package com.xiaotiyun.school.manager.model.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class SubstituteRecordExportEnModel {
    @ExcelProperty("Substitute Date")
    private String substituteDate;
    @ExcelProperty("Substitute Type")
    private String substituteType;
    @ExcelProperty("Lesson")
    private String lessonName;
    @ExcelProperty("Class")
    private String className;
    @ExcelProperty("Subject")
    private String subjectName;
    @ExcelProperty("Original Teacher")
    private String originalTeacherName;
    @ExcelProperty("Substitute Teacher")
    private String substituteTeacherName;
    @ExcelProperty("Remarks")
    private String remark;
}