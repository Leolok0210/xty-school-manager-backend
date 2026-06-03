package com.xiaotiyun.school.manager.model.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class UserImportEnUsModel extends BasicImportModel {
    @ExcelProperty(value = "Username (Required)", index = 0)
    private String uName;
    @ExcelProperty(value = "Phone Number Region", index = 1)
    private String phoneArea;
    @ExcelProperty(value = "Mobile Number", index = 2)
    private String phone;
    @ExcelProperty(value = "Full Name (Required)", index = 3)
    private String userName;
    @ExcelProperty(value = "Department (Required)", index = 4)
    private String deptName;
    @ExcelProperty(value = "User Group (Required)", index = 5)
    private String userGroup;
    @ExcelProperty(value = "User ID (Required)", index = 6)
    private String userNumber;
//    @ExcelProperty(value = "Position (Required)", index = 6)
//    private String position;
    @ExcelProperty(value = "Gender (Required)", index = 7)
    private String gender;
}