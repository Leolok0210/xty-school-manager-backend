package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "仪表不符登记返回对象")
public class DressCodeViolationResModel {
    @ApiModelProperty(value = "主键ID", example = "1")
    private String id;

    @ApiModelProperty(value = "学年", example = "2023-2024")
    private String schoolYear;

    @ApiModelProperty(value = "学段id", example = "1")
    private String semesterId;

    @ApiModelProperty(value = "学段名称", example = "高中")
    private String semesterName;

    @ApiModelProperty(value = "级组ID", example = "1")
    private String classGroupId;

    @ApiModelProperty(value = "级组名称", example = "高一")
    private String classGroupName;

    @ApiModelProperty(value = "班级ID", example = "1")
    private String classId;

    @ApiModelProperty(value = "班级名称", example = "高一(1)班")
    private String className;

    @ApiModelProperty(value = "班内号", example = "1")
    private String studentClassNumber;

    @ApiModelProperty(value = "学生ID", example = "123")
    private String studentId;

    @ApiModelProperty(value = "学生姓名", example = "张三")
    private String studentName;

    @ApiModelProperty(value = "日期", example = "2023-10-01")
    private String violationDate;

    @ApiModelProperty(value = "备注ID", example = "1")
    private String remarkId;

    @ApiModelProperty(value = "备注", example = "未佩戴校徽")
    private String remark;

    @ApiModelProperty(value = "登记人", example = "李四")
    private String registrant;


    @ApiModelProperty(value = "学部", example = "1")
    private Integer department;
}