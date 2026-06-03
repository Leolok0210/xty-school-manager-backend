package com.xiaotiyun.school.manager.model.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class ActivityCoursesImportHeaderEnModel {
    @ExcelProperty("Course Name\n（required）")
    private String courseName;
    @ExcelProperty("Lecturer\n（required）")
    private String teacherName;
    @ExcelProperty("Class location\n（required）")
    private String classRoom;
    @ExcelProperty("Course Name\n（required）")
    private String courseNumber;
    @ExcelProperty("Number of courses\n（required）")
    private String courseTimes;
    @ExcelProperty("Course time\n（required）")
    private String courseTime;
    @ExcelProperty("Course start time\n（required）")
    private String startTime;
    @ExcelProperty("End time of course\n（required）")
    private String endTime;
}