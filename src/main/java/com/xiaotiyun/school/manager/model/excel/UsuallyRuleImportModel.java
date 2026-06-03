package com.xiaotiyun.school.manager.model.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class UsuallyRuleImportModel {

    @ExcelProperty(index = 0)
    private String groupName;// 级组名称

    @ExcelProperty(index = 1)
    private String subjectName;// 科目名称

    @ExcelProperty(index = 2)
    private String type;// 类型

    @ExcelProperty(index = 3)
    private String weight;// 权重单位%，入库需要*100

    private int rowNum;

    private Long groupId;
    private Long subjectId;
    private Long typeId;
}
