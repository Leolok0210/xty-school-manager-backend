package com.xiaotiyun.school.manager.model.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class LeisureActivitiesScoreExportEnModel {
    @ExcelProperty("Class")
    private String className;
    @ExcelProperty("Seat No.")
    private String seatNo;
    @ExcelProperty("Name")
    private String studentName;
    @ExcelProperty("Student ID")
    private String studentNo;
    @ExcelProperty("Course")
    private String courseName;
    @ExcelProperty("Attendance Count")
    private String attendCount;
    @ExcelProperty("Attendance Score")
    private String attendScore;
    @ExcelProperty("Class Performance Score")
    private String lessonScore;
    @ExcelProperty("Total Score")
    private String totalScore;
    @ExcelProperty("Reference Grade")
    private String referenceLevel;
    @ExcelProperty("Final Grade")
    private String finalLevel;
} 