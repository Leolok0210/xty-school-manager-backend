package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "巡堂登记返回对象")
public class PatrolRegistrationResModel {
    @ApiModelProperty(value = "主键ID", example = "1")
    private Long id;

    @ApiModelProperty(value = "学年", example = "2023-2024")
    private String schoolYear;

    @ApiModelProperty(value = "学段id", example = "1")
    private Long semesterId;

    @ApiModelProperty(value = "学段名称", example = "高中")
    private String semesterName;

    @ApiModelProperty(value = "级组ID", example = "1")
    private Long classGroupId;

    @ApiModelProperty(value = "级组名称", example = "高一")
    private String classGroupName;

    @ApiModelProperty(value = "班级ID", example = "1")
    private Long classId;

    @ApiModelProperty(value = "班级名称", example = "高一(1)班")
    private String className;

    @ApiModelProperty(value = "班内号", example = "1")
    private Long studentClassNumber;

    @ApiModelProperty(value = "学生ID", example = "1")
    private Long studentId;

    @ApiModelProperty(value = "学生姓名", example = "张三")
    private String studentName;

    @ApiModelProperty(value = "日期", example = "2023-10-01")
    private String registrationDate;

    @ApiModelProperty(value = "课节ID", example = "1")
    private Long lessonPeriodId;

    @ApiModelProperty(value = "课节", example = "第一节课")
    private String lessonPeriod;

    @ApiModelProperty(value = "登记ID", example = "1")
    private String registrationId;

    @ApiModelProperty(value = "登记内容", example = "未佩戴校徽")
    private String registrationContent;

    @ApiModelProperty(value = "登记人", example = "李四")
    private String registrant;
}