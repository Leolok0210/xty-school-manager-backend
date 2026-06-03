package com.xiaotiyun.school.manager.model.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class UserImportHeaderModel {
    @ExcelProperty("用戶名（必填）")
    private String uName;
    @ExcelProperty("手機號地區")
    private String phoneArea;
    @ExcelProperty("手機號碼")
    private String phone;
    @ExcelProperty("用戶姓名（必填）")
    private String userName;
    @ExcelProperty("所屬部門（必填）")
    private String deptName;
    @ExcelProperty("用戶組（必填）")
    private String userGroup;
    @ExcelProperty("用戶編號（必填）")
    private String userNumber;
//    @ExcelProperty("用戶職務（必选）")
//    private String position;
    @ExcelProperty("性別（必填）")
    private String gender;
}