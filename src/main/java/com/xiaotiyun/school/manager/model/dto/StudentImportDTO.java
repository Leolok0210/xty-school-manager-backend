package com.xiaotiyun.school.manager.model.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class StudentImportDTO {
    /**
     * id
     */
    private Long id;
    /**
     * 学校id
     */
    private Long schoolId;
    /**
     * 班级ID
     */
    private Long classId;
    /**
     * 中文姓名
     */
    private String chineseName;
    /**
     * 外文姓名
     */
    private String englishName;
    /**
     * 文理科(1文科 2理科) 理工商科(1理工科 2理科 3商科)
      */
    private Integer studentType;
    /**
     * 性别(1:男,2:女)
     */
    private Integer gender;
    /**
     * 学生编号
     */
    private String studentNo;
    /**
     * 学生证编号
     */
    private String educationNo;
    /**
     * 座位号
     */
    private Integer seatNo;
    /**
     * 证件类型(1:澳门居民身份证,2:护照,4:港澳台居民居住证,5:外国人永久居留身份证)
     */
    private Integer idType;
    /**
     * 证件编号
     */
    private String idNo;
    /**
     * 出生日期
     */
    private LocalDate birthDate;
    /**
     * 出生地点
     */
    private String birthPlace;
    /**
     * 證件發出地點
     */
    private String idIssuePlace;
    /**
     * 證件發出日期
     */
    private LocalDate idIssueDate;
    /**
     * 證件有效日期
     */
    private LocalDate idValidDate;
    /**
     * 回鄉證編號
     */
    private String reEntryPermitNo;
    /**
     * 逗留許可類型
     */
    private Integer stayType;
    /**
     * 逗留許可發出日期
     */
    private LocalDate stayIssueDate;
    /**
     * 逗留許可有效日期
     */
    private LocalDate stayValidDate;
    /**
     * 國籍
     */
    private String nationality;
    /**
     * 籍貫
     */
    private String nativePlace;
    /**
     * 住址電話
     */
    private String permanentPhone;
    /**
     * 手提電話
     */
    private String mobilePhone;
    /**
     * 常用住址-地區
     */
    private String permanentAddressAreaId;
    /**
     * 常用住址-詳細地址
     */
    private String permanentAddress;
    /**
     * 夜間留宿住址-地區
     */
    private String nightAddressAreaId;
    /**
     * 夜間留宿住址-詳細地址
     */
    private String nightAddress;
    /**
     * 状态(1:在校,2:毕业,3:退学,4:休学,5:转学)
     */
    private Integer status;
    /**
     * 家庭信息
     */
    private StudentFamilyImportDTO familyImportDTO;

    private List<StudentParentImportDTO> parentImportDTOList;

    //學生企微賬號
    private String studentWeChat;
    //學生手機號
    private String studentPhone;
}
