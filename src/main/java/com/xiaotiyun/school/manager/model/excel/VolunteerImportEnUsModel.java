package com.xiaotiyun.school.manager.model.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class VolunteerImportEnUsModel extends BasicImportModel {
    @ExcelProperty(value = "student（required）", index = 0)
    private String studentName;
    @ExcelProperty(value = "Student ID（required）", index = 1)
    private String studentNo;
    @ExcelProperty(value = "Activity Name（required）", index = 2)
    private String activityName;
    @ExcelProperty(value = "Institution Name（required）", index = 3)
    private String organization;
    @ExcelProperty(value = "service date（required）", index = 4)
    private String serviceDate;
    @ExcelProperty(value = "Service hours（required）", index = 5)
    private String serviceHours;
}