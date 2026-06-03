package com.xiaotiyun.school.manager.model.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class UserImportZhTwModel extends BasicImportModel {
    @ExcelProperty(value = "用戶名（必填）", index = 0)
    private String uName;
    @ExcelProperty(value = "手機號地區", index = 1)
    private String phoneArea;
    @ExcelProperty(value = "手機號碼", index = 2)
    private String phone;
    @ExcelProperty(value = "用戶姓名（必填）", index = 3)
    private String userName;
    @ExcelProperty(value = "所屬部門（必填）", index = 4)
    private String deptName;
    @ExcelProperty(value = "用戶組（必填）", index = 5)
    private String userGroup;
    @ExcelProperty(value = "用戶編號（必填）", index = 6)
    private String userNumber;
//    @ExcelProperty(value = "用戶職務（必选）", index = 6)
//    private String position;
    @ExcelProperty(value = "性別（必填）", index = 7)
    private String gender;
}