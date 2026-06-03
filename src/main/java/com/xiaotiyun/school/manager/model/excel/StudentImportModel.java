package com.xiaotiyun.school.manager.model.excel;

import lombok.Data;

@Data
public class StudentImportModel extends BasicImportModel {
    // 中文姓名（必填）
    private String chineseName;
    // 學生編號（必填）
    private String studentNo;
    // 学生证编号（必填）
    private String educationNo;
    // 班內號
    private String seatNo;
    // 級组（必填）
    private String gradeGroup;
    // 班級名稱（必填）
    private String className;
    // 學生類型（下拉选择）
    private String studentType;
    // 外文姓名
    private String englishName;
    // 性别
    private String gender;
    // 出生日期
    private String birthDate;
    // 出生地點
    private String birthPlace;
    // 證件類型
    private String idType;
    // 證件編號
    private String idNo;
    // 證件發出地點
    private String idIssuePlace;
    // 證件發出日期
    private String idIssueDate;
    // 證件有效日期
    private String idValidDate;
    // 回鄉證編號
    private String reEntryPermitNo;
    // 逗留許可類型
    private String stayType;
    // 逗留許可發出日期
    private String stayIssueDate;
    // 逗留許可有效日期
    private String stayValidDate;
    // 國籍
    private String nationality;
    // 籍貫
    private String nativePlace;
    // 住址電話
    private String permanentPhone;
    // 手提電話
    private String mobilePhone;
    // 常用住址-地區
    private String permanentAddressAreaId;
    // 常用住址-詳細地址
    private String permanentAddress;
    // 夜間留宿住址-地區
    private String nightAddressAreaId;
    // 夜間留宿住址-詳細地址
    private String nightAddress;
    // 監護人姓名
    private String guardianName;
    // 監護人聯絡電話
    private String guardianPhone;
    // 監護人流動電話
    private String guardianMobile;
    // 監護人職業
    private String guardianOccupation;
    // 監護人和學生關係
    private String guardianRelation;
    // 監護人住址-地區
    private String guardianAddressAreaId;
    // 監護人住址-詳細地址
    private String guardianAddress;
    // 與監護人同住
    private String liveWithGuardian;
    // 緊急聯絡人姓名（必填）
    private String emergencyContact;
    // 緊急聯絡人與學生關係
    private String emergencyRelation;
    // 緊急聯絡人聯絡電話（必填）
    private String emergencyPhone;
    // 緊急聯絡人住址-地區
    private String emergencyAddressAreaId;
    // 緊急聯絡人住址-詳細地址
    private String emergencyAddress;
    //學生企微賬號
    private String studentWeChat;
    //學生手機號
    private String studentPhone;
    //家長關係一
    private String parentRelationOne;
    //家長手機號
    private String parentPhoneOne;
    //家長姓名
    private String parentName;
    //家長職業
    private String parentOccupation;
    //家長關係二
    private String parentRelationTwo;
    //家長手機號
    private String parentPhoneTwo;
    //家長姓名
    private String parentNameTwo;
    //家長職業
    private String parentOccupationTwo;
    //家長關係三
    private String parentRelationThree;
    //家長手機號
    private String parentPhoneThree;
    //家長姓名
    private String parentNameThree;
    //家長關係四
    private String parentRelationFour;
    //家長手機號
    private String parentPhoneFour;
    //家長姓名
    private String parentNameFour;
}