package com.xiaotiyun.school.manager.model.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class UserExportModel {
    @ExcelProperty("用戶編號")
    private String userNumber;
    @ExcelProperty("用戶名")
    private String loginName;
    @ExcelProperty("用戶姓名")
    private String username;
    @ExcelProperty("手機號")
    private String mobile;
    @ExcelProperty("部門")
    private String deptName;
    @ExcelProperty("用戶組")
    private String userGroupName;
//    @ExcelProperty("用戶類型")
//    private String userType;
//    @ExcelProperty("職務")
//    private String position;
    @ExcelProperty("性別")
    private String gender;
//    @ExcelProperty("狀態")
//    private String status;
}