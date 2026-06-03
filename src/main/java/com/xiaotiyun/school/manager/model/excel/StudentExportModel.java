package com.xiaotiyun.school.manager.model.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class StudentExportModel {
    @ExcelProperty("學年")
    private String schoolYear;
    @ExcelProperty("學生姓名")
    private String studentName;
    @ExcelProperty("性別")
    private String gender;
    @ExcelProperty("學生編號")
    private String studentNo;
    @ExcelProperty("座位號")
    private String seatNo;
    @ExcelProperty("班級名稱")
    private String className;
    @ExcelProperty("級組")
    private String gradeName;
    @ExcelProperty("狀態")
    private String status;
    @ExcelProperty("英文名")
    private String englishName;
    @ExcelProperty("教青局編號")
    private String educationNo;
    @ExcelProperty("國籍")
    private String nationality;
    @ExcelProperty("籍貫")
    private String nativePlace;
    @ExcelProperty("證件類型")
    private String idType;
    @ExcelProperty("證件編號")
    private String idNo;
    @ExcelProperty("常住地址")
    private String permanentAddress;
    @ExcelProperty("手提電話")
    private String mobilePhone;
    @ExcelProperty("住址電話") // 住址電話
    private String permanentPhone;
    @ExcelProperty("退學日期") // 退学日期
    private String outTime;
    @ExcelProperty("退學原因") // 退学原因
    private String outReason;
    @ExcelProperty("升留級情況") // 升留级情况
    private String escalationSituation;
}