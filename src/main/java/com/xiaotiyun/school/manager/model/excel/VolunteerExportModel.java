package com.xiaotiyun.school.manager.model.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class VolunteerExportModel {
    @ExcelProperty("學年")
    private String schoolYear;
    @ExcelProperty("班級名稱")
    private String className;
    @ExcelProperty("學生名稱")
    private String studentName;
    @ExcelProperty("座位號")
    private String seatNo;
    @ExcelProperty("活動名稱")
    private String activityName;
    @ExcelProperty("機構名稱")
    private String organization;
    @ExcelProperty("服務日期")
    private String serviceDate;
    @ExcelProperty("開始時間")
    private String startTime;
    @ExcelProperty("結束時間")
    private String endTime;
    @ExcelProperty("服務時數")
    private String serviceSeconds;
    @ExcelProperty("服務性質")
    private String serviceNature;
    @ExcelProperty("服務類別")
    private String serviceType;
} 