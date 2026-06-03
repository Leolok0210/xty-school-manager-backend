package com.xiaotiyun.school.manager.model.res;

import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "平时成绩权重配置导入数据类")
public class StudentUsuallyRuleImportResModel {

    @ApiModelProperty(value = "级组名称")
    @ExcelProperty(index = 0)
    private String gradeGroupName;

    @ApiModelProperty(value = "科目名称")
    @ExcelProperty(index = 1)
    private String subjectName;

    @ApiModelProperty(value = "平时成绩类型名称")
    @ExcelProperty(index = 2)
    private String typeName;

    @ApiModelProperty(value = "权重单位%，结果*100")
    @ExcelProperty(index = 3)
    private Integer weight;

}