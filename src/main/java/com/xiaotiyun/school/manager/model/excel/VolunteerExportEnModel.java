package com.xiaotiyun.school.manager.model.excel;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
@ExcelIgnoreUnannotated
public class VolunteerExportEnModel {
    @ExcelProperty("Academic Year")
    private String schoolYear;
    @ExcelProperty("Class Name")
    private String className;
    @ExcelProperty("Student Name")
    private String studentName;
    @ExcelProperty("Seat Number")
    private String seatNo;
    @ExcelProperty("Activity Name")
    private String activityName;
    @ExcelProperty("Organization Name")
    private String organization;
    @ExcelProperty("Service Date")
    private String serviceDate;
    @ExcelProperty("Start Time")
    private String startTime;
    @ExcelProperty("End Time")
    private String endTime;
    @ExcelProperty("Service Hours")
    private String serviceSeconds;
    @ExcelProperty("Service Nature")
    private String serviceNature;
    @ExcelProperty("Service Category")
    private String serviceType;
}
