package com.xiaotiyun.school.manager.model.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class StudentQualityScoreImportModel {

    //座位号
    @ExcelProperty("座位号")
    private String seatNumber;
    //中文姓名
    @ExcelProperty("中文姓名")
    private String chineseName;
    //学生编号
    @ExcelProperty("学生编号")
    private String studentNumber;
    //素质项目
    @ExcelProperty("素质项目")
    private String qualityProject;
    //素质项目评分
    @ExcelProperty("素质项目评分")
    private String qualityProjectScore;

    private Integer rowIndex;
}