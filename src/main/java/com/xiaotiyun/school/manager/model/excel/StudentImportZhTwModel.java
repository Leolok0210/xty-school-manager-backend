package com.xiaotiyun.school.manager.model.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class StudentImportZhTwModel extends BasicImportModel {
    @ExcelProperty(value = "中文姓名（必填）", index = 0)
    private String chineseName;
    @ExcelProperty(value = "學生編號（必填）", index = 1)
    private String studentNo;
    @ExcelProperty(value = "學生證編號（必填）", index = 2)
    private String educationNo;
    @ExcelProperty(value = "班內號", index = 3)
    private String seatNo;
    @ExcelProperty(value = "級组（必填，下拉选择）", index = 4)
    private String gradeGroup;
    @ExcelProperty(value = "班級名稱（必填）", index = 5)
    private String className;
    @ExcelProperty(value = "學生類型（下拉选择）", index = 6)
    private String studentType;
    @ExcelProperty(value = "外文姓名", index = 7)
    private String englishName;
    @ExcelProperty(value = "性別（下拉选择）", index = 8)
    private String gender;
    @ExcelProperty(value = "出生日期", index = 9)
    private String birthDate;
    @ExcelProperty(value = "出生地點（下拉选择）", index = 10)
    private String birthPlace;
    @ExcelProperty(value = "證件類別（下拉选择）", index = 11)
    private String idType;
    @ExcelProperty(value = "證件編號", index = 12)
    private String idNo;
    @ExcelProperty(value = "證件發出地點（下拉选择）", index = 13)
    private String idIssuePlace;
    @ExcelProperty(value = "證件發出日期", index = 14)
    private String idIssueDate;
    @ExcelProperty(value = "證件有效日期", index = 15)
    private String idValidDate;
    @ExcelProperty(value = "回鄉證編號", index = 16)
    private String reEntryPermitNo;
    @ExcelProperty(value = "逗留許可類型（下拉选择）", index = 17)
    private String stayType;
    @ExcelProperty(value = "逗留許可發出日期", index = 18)
    private String stayIssueDate;
    @ExcelProperty(value = "逗留許可有效日期", index = 19)
    private String stayValidDate;
    @ExcelProperty(value = "國籍（下拉选择）", index = 20)
    private String nationality;
    @ExcelProperty(value = "籍貫", index = 21)
    private String nativePlace;
    @ExcelProperty(value = "住址電話", index = 22)
    private String permanentPhone;
    @ExcelProperty(value = "手提電話", index = 23)
    private String mobilePhone;
    @ExcelProperty(value = "常用住址-地區（下拉选择）", index = 24)
    private String permanentAddressAreaId;
    @ExcelProperty(value = "常用住址-詳細地址", index = 25)
    private String permanentAddress;
    @ExcelProperty(value = "夜間留宿住址-地區（下拉选择）", index = 26)
    private String nightAddressAreaId;
    @ExcelProperty(value = "夜間留宿住址-詳細地址", index = 27)
    private String nightAddress;
    @ExcelProperty(value = "監護人姓名", index = 28)
    private String guardianName;
    @ExcelProperty(value = "監護人聯絡電話", index = 29)
    private String guardianPhone;
    @ExcelProperty(value = "監護人流動電話", index = 30)
    private String guardianMobile;
    @ExcelProperty(value = "監護人職業", index = 31)
    private String guardianOccupation;
    @ExcelProperty(value = "監護人和學生關係（下拉选择）", index = 32)
    private String guardianRelation;
    @ExcelProperty(value = "監護人住址-地區（下拉选择）", index = 33)
    private String guardianAddressAreaId;
    @ExcelProperty(value = "監護人住址-詳細地址", index = 34)
    private String guardianAddress;
    @ExcelProperty(value = "與監護人同住（下拉选择）", index = 35)
    private String liveWithGuardian;
    @ExcelProperty(value = "緊急聯絡人姓名（必填）", index = 36)
    private String emergencyContact;
    @ExcelProperty(value = "緊急聯絡人與學生關係（下拉选择）", index = 37)
    private String emergencyRelation;
    @ExcelProperty(value = "緊急聯絡人聯絡電話（必填）", index = 38)
    private String emergencyPhone;
    @ExcelProperty(value = "緊急聯絡人住址-地區（下拉选择）", index = 39)
    private String emergencyAddressAreaId;
    @ExcelProperty(value = "緊急聯絡人住址-詳細地址", index = 40)
    private String emergencyAddress;
    @ExcelProperty(value = "學生企微賬號", index = 41)
    private String studentWeChat;
    @ExcelProperty(value = "學生手機號", index = 42)
    private String studentPhone;
    @ExcelProperty(value = "家長關係一（下拉选择）", index = 43)
    private String parentRelationOne;
    @ExcelProperty(value = "家長手機號", index = 44)
    private String parentPhoneOne;
    @ExcelProperty(value = "家長姓名", index = 45)
    private String parentName;
    @ExcelProperty(value = "家長職業", index = 46)
    private String parentOccupation;
    @ExcelProperty(value = "家長關係二（下拉选择）", index = 47)
    private String parentRelationTwo;
    @ExcelProperty(value = "家長手機號", index = 48)
    private String parentPhoneTwo;
    @ExcelProperty(value = "家長姓名", index = 49)
    private String parentNameTwo;
    @ExcelProperty(value = "家長職業", index = 50)
    private String parentOccupationTwo;
    @ExcelProperty(value = "家長關係三（下拉选择）", index = 51)
    private String parentRelationThree;
    @ExcelProperty(value = "家長姓名", index = 52)
    private String parentNameThree;
    @ExcelProperty(value = "家長手機號", index = 53)
    private String parentPhoneThree;
    @ExcelProperty(value = "家長關係四（下拉选择）", index = 54)
    private String parentRelationFour;
    @ExcelProperty(value = "家長姓名", index = 55)
    private String parentNameFour;
    @ExcelProperty(value = "家長手機號", index = 56)
    private String parentPhoneFour;

}