package com.xiaotiyun.school.manager.model.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class SubjectImportModel {
    @ExcelProperty("科目編號（必填）")
    private String subjectNumber;

    @ExcelProperty("科目名稱（必填）")
    private String subjectName;

    @ExcelProperty("英文名称")
    private String subjectEnglishName;

    @ExcelProperty("单位\n" +
            "（格式：数字，1-100）")
    private String unit;
    @ExcelProperty("\"适用学部（必填）\n" +
            "支持输入：幼稚园、小学、中学\n" +
            "包含多个时：逗号“，”隔开\"\n")
    private String scope;

    private Integer rowIndex;
}