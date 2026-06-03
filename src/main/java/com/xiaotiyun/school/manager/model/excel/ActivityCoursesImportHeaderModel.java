package com.xiaotiyun.school.manager.model.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class ActivityCoursesImportHeaderModel {
    @ExcelProperty("課程名額\n（必填）")
    private String courseName;
    @ExcelProperty("授課教師\n（下拉必填）")
    private String teacherName;
    @ExcelProperty("上課地點\n（下拉必填）")
    private String classRoom;
    @ExcelProperty("課程名額\n（必填）")
    private String courseNumber;
    @ExcelProperty("課程次數\n（必填）")
    private String courseTimes;
    @ExcelProperty("課程時間\n（下拉必填）")
    private String courseTime;
    @ExcelProperty("課程開始時間\n（必填）")
    private String startTime;
    @ExcelProperty("課程結束時間\n（必填）")
    private String endTime;
}