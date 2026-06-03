package com.xiaotiyun.school.manager.model.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class UserExportEnModel {
    @ExcelProperty("User Number")
    private String userNumber;
    @ExcelProperty("Login Name")
    private String loginName;
    @ExcelProperty("Username")
    private String username;
    @ExcelProperty("Mobile Phone")
    private String mobile;
    @ExcelProperty("Department")
    private String deptName;
    @ExcelProperty("User Group")
    private String userGroupName;
//    @ExcelProperty("User Type")
//    private String userType;
//    @ExcelProperty("Position")
//    private String position;
    @ExcelProperty("Gender")
    private String gender;
//    @ExcelProperty("Status")
//    private String status;
}