package com.xiaotiyun.school.manager.model.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class StudentPromotionExportEnModel {
    @ExcelProperty("School Year")
    private String schoolYear;
    @ExcelProperty("Class Name")
    private String className;
    @ExcelProperty("Seat Number")
    private String seatNo;
    @ExcelProperty("Student Name")
    private String studentName;
    @ExcelProperty("Promotion/Retention")
    private String promotionType;
}