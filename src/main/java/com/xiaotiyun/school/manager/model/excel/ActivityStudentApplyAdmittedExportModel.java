package com.xiaotiyun.school.manager.model.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class ActivityStudentApplyAdmittedExportModel {
    @ExcelProperty("班級")
    private String className;
    @ExcelProperty("班內號")
    private String seatNo;
    @ExcelProperty("姓名")
    private String studentName;
    @ExcelProperty("學生編號")
    private String studentNo;
    @ExcelProperty("匹配課程")
    private String courseName;
    @ExcelProperty("錄取階段")
    private String admissionStage;
    @ExcelProperty("錄取方式")
    private String admissionMethod;
}