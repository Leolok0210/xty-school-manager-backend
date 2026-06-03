package com.xiaotiyun.school.manager.model.excel;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
@ExcelIgnoreUnannotated
public class ExternalCompetitionExportEnDTO {
    @ExcelProperty("Student ID")
    private String educationNo;
    @ExcelProperty("School Student Number")
    private String studentNo;
    @ExcelProperty("Class")
    private String className;
    @ExcelProperty("Seat Number")
    private String seatNo;
    @ExcelProperty("Student Name")
    private String studentName;
    @ExcelProperty("Category")
    private String categoryName;
    @ExcelProperty("Competition/Activity Name")
    private String name;
    @ExcelProperty("Project/Group")
    private String groupName;
    @ExcelProperty("Award")
    private String prize;
    @ExcelProperty("Award Level")
    private String awardsName;
    @ExcelProperty("Organizer")
    private String organizer;
    @ExcelProperty("Activity Area")
    private String activityArea;
    @ExcelProperty("Region")
    private String area;
    @ExcelProperty("Advisor")
    private String advisor;
    @ExcelProperty("Individual/Group")
    private String competitionType;
    @ExcelProperty("Activity Start Date")
    private String startTime;
    @ExcelProperty("Award Date")
    private String prizeTime;
    @ExcelProperty("Is Representative")
    private String representative;
    @ExcelProperty("Recommendation")
    private String awardsRemark;
    @ExcelProperty("Filler")
    private String createUserName;
    @ExcelProperty("Highest Recommendation (Auto-generated)")
    private String autoAwardsRemark;
    @ExcelProperty("Final Recognition")
    private String finalAwards;
    @ExcelProperty("Final Recognition Points")
    private String finalAwardsPoints;
    @ExcelProperty("Review Remarks")
    private String approveRemark;
}
