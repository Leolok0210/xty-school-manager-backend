package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "大息小息表現登記返回对象")
public class BigLittleRestResModel {
    @ApiModelProperty(value = "主键ID", example = "1")
    private String id;

    @ApiModelProperty(value = "学年", example = "2023-2024")
    private String schoolYear;

    @ApiModelProperty(value = "学段ID", example = "1")
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

    @ApiModelProperty(value = "学生ID", example = "123")
    private String studentId;

    @ApiModelProperty(value = "学生姓名", example = "张三")
    private String studentName;

    @ApiModelProperty(value = "日期", example = "2023-10-01")
    private String registrationDate;

    @ApiModelProperty(value = "类型", example = "大息")
    private String type;

    @ApiModelProperty(value = "大息小息ID", example = "1")
    private String registrationId;

    @ApiModelProperty(value = "大息小息表現", example = "未佩戴校徽")
    private String registrationContent;

    @ApiModelProperty(value = "登记人", example = "李四")
    private String registrant;
}