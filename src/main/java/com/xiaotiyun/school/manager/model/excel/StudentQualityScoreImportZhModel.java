package com.xiaotiyun.school.manager.model.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class StudentQualityScoreImportZhModel {
    @ExcelProperty("座位號")
    private String seatNumber;
    @ExcelProperty("中文姓名")
    private String chineseName;
    @ExcelProperty("學生編號")
    private String studentNumber;
    @ExcelProperty("素质项目")
    private String qualityProject;
    @ExcelProperty("素质项目评分")
    private String qualityProjectScore;
    @ExcelProperty("所属学部")
    private String department;
    private Integer rowIndex;
}