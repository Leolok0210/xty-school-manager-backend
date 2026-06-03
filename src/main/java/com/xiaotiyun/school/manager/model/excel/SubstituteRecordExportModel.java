package com.xiaotiyun.school.manager.model.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class SubstituteRecordExportModel {
    @ExcelProperty("代課日期")
    private String substituteDate;
    @ExcelProperty("代課類型")
    private String substituteType;
    @ExcelProperty("節數")
    private String lessonName;
    @ExcelProperty("代課班級")
    private String className;
    @ExcelProperty("代課科目")
    private String subjectName;
    @ExcelProperty("原科任老師")
    private String originalTeacherName;
    @ExcelProperty("代課老師")
    private String substituteTeacherName;
    @ExcelProperty("備註")
    private String remark;
}