package com.xiaotiyun.school.manager.model.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class SysClassImportModel {
    @ExcelProperty("級組（必选，下拉选择）")
    private String gradeGroup;
    @ExcelProperty("班級名稱")
    private String className;

    @ExcelProperty("班級序號（必填）")
    private String classSerialNumber;

//    @ExcelProperty("是否專業班（必填）")
//    private String professionalVersion;

    @ExcelProperty("文理科（下拉选择）")
    private String artsScience;

    @ExcelProperty("專業名稱")
    private String professional;

    @ExcelProperty("班主任用户编号（必填）")
    private String headTeacher;

    private Integer rowIndex;
}