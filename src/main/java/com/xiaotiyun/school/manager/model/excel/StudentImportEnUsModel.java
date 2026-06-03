package com.xiaotiyun.school.manager.model.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class StudentImportEnUsModel extends BasicImportModel {
    @ExcelProperty(value = "Chinese Name (Required)", index = 0)
    private String chineseName;
    @ExcelProperty(value = "Student ID (Required)", index = 1)
    private String studentNo;
    @ExcelProperty(value = "Student ID Number (Required)", index = 2)
    private String educationNo;
    @ExcelProperty(value = "Seat Number", index = 3)
    private String seatNo;
    @ExcelProperty(value = "Grade Group (required，Select from dropdown)", index = 4)
    private String gradeGroup;
    @ExcelProperty(value = "Class (required)", index = 5)
    private String className;
    @ExcelProperty(value = "Student Type (Select from dropdown)", index = 6)
    private String studentType;
    @ExcelProperty(value = "Foreign Name", index = 7)
    private String englishName;
    @ExcelProperty(value = "Gender（Select from dropdown）", index = 8)
    private String gender;
    @ExcelProperty(value = "Date of Birth", index = 9)
    private String birthDate;
    @ExcelProperty(value = "Place of Birth（Select from dropdown）", index = 10)
    private String birthPlace;
    @ExcelProperty(value = "Document Type（Select from dropdown）", index = 11)
    private String idType;
    @ExcelProperty(value = "Document Number", index = 12)
    private String idNo;
    @ExcelProperty(value = "Document Issuing Place（Select from dropdown）", index = 13)
    private String idIssuePlace;
    @ExcelProperty(value = "Document Issuing Date", index = 14)
    private String idIssueDate;
    @ExcelProperty(value = "Document Expiry Date", index = 15)
    private String idValidDate;
    @ExcelProperty(value = "Home Return Permit Number", index = 16)
    private String reEntryPermitNo;
    @ExcelProperty(value = "Type of Stay Permit（Select from dropdown）", index = 17)
    private String stayType;
    @ExcelProperty(value = "Stay Permit Issuing Date", index = 18)
    private String stayIssueDate;
    @ExcelProperty(value = "Stay Permit Expiry Date", index = 19)
    private String stayValidDate;
    @ExcelProperty(value = "Nationality（Select from dropdown）", index = 20)
    private String nationality;
    @ExcelProperty(value = "Ancestral Origin", index = 21)
    private String nativePlace;
    @ExcelProperty(value = "Home Phone", index = 22)
    private String permanentPhone;
    @ExcelProperty(value = "Mobile Phone", index = 23)
    private String mobilePhone;
    @ExcelProperty(value = "Usual Address - District（Select from dropdown）", index = 24)
    private String permanentAddressAreaId;
    @ExcelProperty(value = "Usual Address - Detailed Address", index = 25)
    private String permanentAddress;
    @ExcelProperty(value = "Night Accommodation - District（Select from dropdown）", index = 26)
    private String nightAddressAreaId;
    @ExcelProperty(value = "Night Accommodation - Detailed Address", index = 27)
    private String nightAddress;
    @ExcelProperty(value = "Guardian's Name", index = 28)
    private String guardianName;
    @ExcelProperty(value = "Guardian's Contact Phone", index = 29)
    private String guardianPhone;
    @ExcelProperty(value = "Guardian's Mobile Phone", index = 30)
    private String guardianMobile;
    @ExcelProperty(value = "Guardian's Occupation", index = 31)
    private String guardianOccupation;
    @ExcelProperty(value = "Guardian's Relationship to Student（Select from dropdown）", index = 32)
    private String guardianRelation;
    @ExcelProperty(value = "Guardian's Address - District（Select from dropdown）", index = 33)
    private String guardianAddressAreaId;
    @ExcelProperty(value = "Guardian's Address - Detailed Address", index = 34)
    private String guardianAddress;
    @ExcelProperty(value = "Living with Guardian（Select from dropdown）", index = 35)
    private String liveWithGuardian;
    @ExcelProperty(value = "Emergency Contact Name(Required)", index = 36)
    private String emergencyContact;
    @ExcelProperty(value = "Emergency Contact Relationship to Student（Select from dropdown）", index = 37)
    private String emergencyRelation;
    @ExcelProperty(value = "Emergency Contact Phone (Required)", index = 38)
    private String emergencyPhone;
    @ExcelProperty(value = "Emergency Contact Address - District（Select from dropdown）", index = 39)
    private String emergencyAddressAreaId;
    @ExcelProperty(value = "Emergency Contact Address - Detailed Address", index = 40)
    private String emergencyAddress;
    @ExcelProperty(value = "Student WeCom Account", index = 41)
    private String studentWeChat;
    @ExcelProperty(value = "Student Mobile Number", index = 42)
    private String studentPhone;
    @ExcelProperty(value = "Parent Relationship 1（Select from dropdown）", index = 43)
    private String parentRelationOne;
    @ExcelProperty(value = "Parent Mobile Number", index = 44)
    private String parentPhoneOne;
    @ExcelProperty(value = "Parent Name", index = 45)
    private String parentName;
    @ExcelProperty(value = "Parent Occupation", index = 46)
    private String parentOccupation;
    @ExcelProperty(value = "Parent Relationship 2（Select from dropdown）", index = 47)
    private String parentRelationTwo;
    @ExcelProperty(value = "Parent Mobile Number", index = 48)
    private String parentPhoneTwo;
    @ExcelProperty(value = "Parent Name", index = 49)
    private String parentNameTwo;
    @ExcelProperty(value = "Parent Occupation", index = 50)
    private String parentOccupationTwo;
    @ExcelProperty(value = "Parent Relationship 3（Select from dropdown）", index = 51)
    private String parentRelationThree;
    @ExcelProperty(value = "Parent Name", index = 52)
    private String parentNameThree;
    @ExcelProperty(value = "Parent Mobile Number", index = 53)
    private String parentPhoneThree;
    @ExcelProperty(value = "Parent Relationship 4（Select from dropdown）", index = 54)
    private String parentRelationFour;
    @ExcelProperty(value = "Parent Name", index = 55)
    private String parentNameFour;
    @ExcelProperty(value = "Parent Mobile Number", index = 56)
    private String parentPhoneFour;

}