package com.xiaotiyun.school.manager.model.dto;

import lombok.Data;

@Data
public class StudentFamilyImportDTO {
    // 父親姓名
    private String fatherName;
    // 父親聯絡電話
    private String fatherPhone;
    // 父親職業
    private String fatherOccupation;
    // 母親姓名
    private String motherName;
    // 母親聯絡電話
    private String motherPhone;
    // 母親職業
    private String motherOccupation;
    // 監護人姓名
    private String guardianName;
    // 監護人聯絡電話
    private String guardianPhone;
    // 監護人流動電話
    private String guardianMobile;
    // 監護人職業
    private String guardianOccupation;
    // 監護人和學生關係
    private Integer guardianRelation;
    // 監護人住址-地區
    private String guardianAddressAreaId;
    // 監護人住址-詳細地址
    private String guardianAddress;
    // 與監護人同住
    private Integer liveWithGuardian;
    // 緊急聯絡人姓名（必填）
    private String emergencyContact;
    // 緊急聯絡人與學生關係
    private Integer emergencyRelation;
    // 緊急聯絡人聯絡電話（必填）
    private String emergencyPhone;
    // 緊急聯絡人住址-地區
    private String emergencyAddressAreaId;
    // 緊急聯絡人住址-詳細地址
    private String emergencyAddress;
}
