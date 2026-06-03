package com.xiaotiyun.school.manager.model.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class ActivityStudentApplyNotRegisteredExportEnModel {
    @ExcelProperty("Class")
    private String className;
    @ExcelProperty("Seat No.")
    private String seatNo;
    @ExcelProperty("Name")
    private String studentName;
    @ExcelProperty("Student ID")
    private String studentNo;
    @ExcelProperty("Admission Status")
    private String admissionStatus;
    @ExcelProperty("Admission Stage")
    private String admissionStage;
    @ExcelProperty("Admission Method")
    private String admissionMethod;
}