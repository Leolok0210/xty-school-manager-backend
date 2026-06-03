package com.xiaotiyun.school.manager.model.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class VolunteerImportZhTwModel extends BasicImportModel {
    @ExcelProperty(value = "學生（必填）", index = 0)
    private String studentName;
    @ExcelProperty(value = "學生編號（必填）", index = 1)
    private String studentNo;
    @ExcelProperty(value = "活動名稱（必填）", index = 2)
    private String activityName;
    @ExcelProperty(value = "機構名稱（必填）", index = 3)
    private String organization;
    @ExcelProperty(value = "服務日期（必填）", index = 4)
    private String serviceDate;
    @ExcelProperty(value = "服務時數（必填）", index = 5)
    private String serviceHours;
}