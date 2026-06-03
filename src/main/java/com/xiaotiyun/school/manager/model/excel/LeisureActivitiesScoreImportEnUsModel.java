package com.xiaotiyun.school.manager.model.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class LeisureActivitiesScoreImportEnUsModel extends BasicImportModel {
    @ExcelProperty(value = "course（required）", index = 0)
    private String courseName;
    @ExcelProperty(value = "student（required）", index = 1)
    private String studentName;
    @ExcelProperty(value = "Student code（required）", index = 2)
    private String studentNo;
    @ExcelProperty(value = "Attendance frequency（required）", index = 3)
    private String attendCount;
    @ExcelProperty(value = "Classroom performance score（required）", index = 4)
    private String lessonScore;
}