package com.xiaotiyun.school.manager.model.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class UserImportHeaderUsModel {
    @ExcelProperty("Username (Required)")
    private String uName;
    @ExcelProperty("Phone Number Region")
    private String phoneArea;
    @ExcelProperty("Mobile Number")
    private String phone;
    @ExcelProperty("Full Name (Required)")
    private String userName;
    @ExcelProperty("Department (Required)")
    private String deptName;
    @ExcelProperty("User Group (Required)")
    private String userGroup;
    @ExcelProperty("User ID (Required)")
    private String userNumber;
//    @ExcelProperty("Position (Required)")
//    private String position;
    @ExcelProperty("Gender (Required)")
    private String gender;
}