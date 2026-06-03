package com.xiaotiyun.school.manager.model.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class LeisureActivitiesScoreImportZhTwModel extends BasicImportModel {
    @ExcelProperty(value = "課程（必填）", index = 0)
    private String courseName;
    @ExcelProperty(value = "學生（必填）", index = 1)
    private String studentName;
    @ExcelProperty(value = "學生編號（必填）", index = 2)
    private String studentNo;
    @ExcelProperty(value = "出席次數（必填）", index = 3)
    private String attendCount;
    @ExcelProperty(value = "課節表現分數（必填）", index = 4)
    private String lessonScore;
}