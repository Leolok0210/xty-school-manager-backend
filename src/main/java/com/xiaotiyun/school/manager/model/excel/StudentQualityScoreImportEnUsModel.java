package com.xiaotiyun.school.manager.model.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class StudentQualityScoreImportEnUsModel {
    @ExcelProperty("Seat Number")
    private String seatNumber;
    @ExcelProperty("Chinese Name")
    private String chineseName;
    @ExcelProperty("Student Number")
    private String studentNumber;
    @ExcelProperty("Quality Project")
    private String qualityProject;
    @ExcelProperty("Score")
    private String qualityProjectScore;
    @ExcelProperty("Department")
    private String department;
    private Integer rowIndex;
}