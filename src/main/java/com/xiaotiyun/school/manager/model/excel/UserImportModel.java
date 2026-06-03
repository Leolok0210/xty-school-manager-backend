package com.xiaotiyun.school.manager.model.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class UserImportModel extends BasicImportModel {
    @ExcelProperty("用户名")
    private String uName;
    @ExcelProperty("手机号地区")
    private String phoneArea;
    @ExcelProperty("手机号码")
    private String phone;
    @ExcelProperty("用户姓名")
    private String userName;
    @ExcelProperty("所属部门")
    private String deptName;
    @ExcelProperty("用户组")
    private String userGroup;
    @ExcelProperty("用户编号")
    private String userNumber;
//    @ExcelProperty("用户职务")
//    private String position;
    @ExcelProperty("性别")
    private String gender;
}