package com.xiaotiyun.school.manager.model.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class SysClassZhImportModel {
    @ExcelProperty(value = "級組（必选，下拉选择）", index = 0)
    private String gradeGroup;
    @ExcelProperty(value = "班級序號（必填）",index = 1)
    private String classSerialNumber;
    @ExcelProperty(value = "班級名稱", index = 2)
    private String className;
//    @ExcelProperty(value = "是否專業班（必填）", index = 3)
//    private String professionalVersion;

    @ExcelProperty(value = "文理科（下拉选择）", index = 3)
    private String artsScience;

    @ExcelProperty(value = "專業名稱", index = 4)
    private String professional;

    @ExcelProperty(value = "班主任用户编号（必填）", index = 5)
    private String headTeacher;

    private Integer rowIndex;
}