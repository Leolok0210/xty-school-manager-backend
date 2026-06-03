package com.xiaotiyun.school.manager.model.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class LeisureActivitiesScoreExportModel {
    @ExcelProperty("班級")
    private String className;
    @ExcelProperty("班內號")
    private String seatNo;
    @ExcelProperty("姓名")
    private String studentName;
    @ExcelProperty("學生編號")
    private String studentNo;
    @ExcelProperty("課程")
    private String courseName;
    @ExcelProperty("出席次數")
    private String attendCount;
    @ExcelProperty("出席率分數")
    private String attendScore;
    @ExcelProperty("課節表現分數")
    private String lessonScore;
    @ExcelProperty("總分數")
    private String totalScore;
    @ExcelProperty("參考成績等級")
    private String referenceLevel;
    @ExcelProperty("最終成績等級")
    private String finalLevel;
} 