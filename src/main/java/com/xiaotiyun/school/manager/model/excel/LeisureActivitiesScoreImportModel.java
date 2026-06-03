package com.xiaotiyun.school.manager.model.excel;

import lombok.Data;

@Data
public class LeisureActivitiesScoreImportModel extends BasicImportModel {
    // 課程（必填）
    private String courseName;
    // 學生（必填）
    private String studentName;
    // 學生編號（必填）
    private String studentNo;
    // 出席次數（必填）
    private String attendCount;
    // 課節表現分數（必填）
    private String lessonScore;
}