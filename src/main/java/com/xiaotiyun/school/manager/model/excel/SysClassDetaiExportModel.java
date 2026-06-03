package com.xiaotiyun.school.manager.model.excel;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ExampleProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SysClassDetaiExportModel {
    @ExcelIgnore
    private Long id;
    @ExcelProperty(value = "學年", index = 0)
    @ApiModelProperty(value = "學年")
    private String sid;

    @ExcelProperty(value = "班級編號", index = 1)
    @ApiModelProperty(value = "班級編號")
    private String classNumber;

    @ExcelProperty(value = "班級名稱", index = 2)
    @ApiModelProperty(value = "班級名稱")
    private String className;

    @ExcelProperty(value = "班級序號", index = 3)
    @ApiModelProperty(value = "班級序號")
    private Integer classSerialNumber;

    @ExcelProperty(value = "級組", index = 4)
    @ApiModelProperty(value = "級組")
    private String gradeGroupName;

    @ExcelProperty(value = "是否專業班", index = 5)
    @ApiModelProperty(value = "是否專業班")
    private String professionalVersionName;

    @ExcelProperty(value = "文/理科", index = 6)
    @ApiModelProperty(value = "文/理科")
    private String artsScienceName;

    @ExcelProperty(value = "專業名稱", index = 7)
    @ApiModelProperty(value = "專業名稱")
    private String professionalName;

    @ExcelProperty(value = "班主任", index = 8)
    @ApiModelProperty(value = "班主任")
    private String headTeacherName;
    @ExcelIgnore
    private Integer artsScience;
    @ExcelIgnore
    private Integer professionalVersion;
    @ExcelIgnore
    private Integer department;
    @ExcelIgnore
    private Long gradeGroup;
    @ExcelIgnore
    private Long professionalId;
    @ExcelIgnore
    private Long headTeacher;
    @ExcelIgnore
    private Long schoolId;
}