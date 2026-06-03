package com.xiaotiyun.school.manager.model.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class StudentMedicalRecordImportZhModel {
    /**
     * 學生中文姓名（必填）
     */
    @ExcelProperty("學生中文姓名（必填）")
    private String studentName;

    /**
     * 學生編號（必填）
     */
    @ExcelProperty("學生編號（必填）")
    private String studentNumber;

    /**
     * 就診日期（必填）
     */
    @ExcelProperty("就診日期（必填）")
    private String consultationDate;

    /**
     * 就診時間（必填）
     */
    @ExcelProperty("就診時間（必填）")
    private String consultationTime;

    /**
     * 处理，必填字段
     */
    @ExcelProperty("處理（必填）")
    private String treatment;

    /**
     * 备注，必填字段
     */
    @ExcelProperty("備註（必填）")
    private String notes;

    /**
     * 体温
     */
    @ExcelProperty("體溫")
    private String temperature;

    /**
     * 是否发热
     */
    @ExcelProperty("發熱")
    private String fever;

    /**
     * 是否咳嗽
     */
    @ExcelProperty("咳嗽")
    private String cough;

    /**
     * 是否流涕
     */
    @ExcelProperty("流涕")
    private String runnyNose;

    /**
     * 是否咽痛
     */
    @ExcelProperty("咽痛")
    private String soreThroat;

    /**
     * 是否头晕
     */
    @ExcelProperty("頭暈")
    private String dizziness;

    /**
     * 是否头痛
     */
    @ExcelProperty("頭痛")
    private String headache;

    /**
     * 是否流鼻血
     */
    @ExcelProperty("流鼻血")
    private String nosebleed;

    /**
     * 是否恶心
     */
    @ExcelProperty("噁心")
    private String nausea;

    /**
     * 呕吐次数
     */
    @ExcelProperty("嘔吐次數")
    private String vomitingCount;

    /**
     * 是否腹痛
     */
    @ExcelProperty("腹痛")
    private String abdominalPain;

    /**
     * 腹泻次数
     */
    @ExcelProperty("腹瀉次數")
    private String diarrheaCount;

    /**
     * 主诉现病史
     */
    @ExcelProperty("其他症狀")
    private String chiefComplaint;

    private Integer excelLineNo;
}
