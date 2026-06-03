package com.xiaotiyun.school.manager.model.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class StudentPromotionExportModel {
    @ExcelProperty("學年")
    private String schoolYear;
    @ExcelProperty("班級名稱")
    private String className;
    @ExcelProperty("班內號")
    private String seatNo;
    @ExcelProperty("學生姓名")
    private String studentName;
    @ExcelProperty("升留級/帶科")
    private String promotionType;
}