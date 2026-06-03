package com.xiaotiyun.school.manager.model.excel;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SysClassDetaiExportEnModel {
    @ExcelIgnore
    private Long id;
    @ExcelProperty(value = "Academic Year", index = 0)
    @ApiModelProperty(value = "學年")
    private String sid;

    @ExcelProperty(value = "Class ID", index = 1)
    @ApiModelProperty(value = "班級編號")
    private String classNumber;

    @ExcelProperty(value = "Class Name", index = 2)
    @ApiModelProperty(value = "班級名稱")
    private String className;

    @ExcelProperty(value = "Class Serial Number", index = 3)
    @ApiModelProperty(value = "班級序號")
    private Integer classSerialNumber;

    @ExcelProperty(value = "Grade Group", index = 4)
    @ApiModelProperty(value = "級組")
    private String gradeGroupName;

    @ExcelProperty(value = "Is Major Class", index = 5)
    @ApiModelProperty(value = "是否專業班")
    private String professionalVersionName;

    @ExcelProperty(value = "Stream", index = 6)
    @ApiModelProperty(value = "文/理科")
    private String artsScienceName;

    @ExcelProperty(value = "Major Name", index = 7)
    @ApiModelProperty(value = "專業名稱")
    private String professionalName;

    @ExcelProperty(value = "Homeroom Teacher", index = 8)
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
