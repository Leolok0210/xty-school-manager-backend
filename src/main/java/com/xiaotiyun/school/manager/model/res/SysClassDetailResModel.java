package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("班级详情返回信息")
public class SysClassDetailResModel {
    @ApiModelProperty("ID")
    private Long id;

    @ApiModelProperty("学部(1:幼稚园 2:小学 3:中学)")
    private Integer department;

    @ApiModelProperty("级组")
    private Long gradeGroup;

    @ApiModelProperty("级组")
    private String gradeGroupName;

    @ApiModelProperty("文科理科：0-不分科；1-专业分科；2-文理分科 3-理工商分科")
    private Integer professionalSubject;

    @ApiModelProperty("班级序号")
    private Integer classSerialNumber;

    @ApiModelProperty("班级编号")
    private String classNumber;

    @ApiModelProperty("班级名称")
    private String className;

//    @ApiModelProperty("是否专业版 (0. 否，1. 是)")
//    private Integer professionalVersion;

    @ApiModelProperty("文理科(1文科 2理科) 理工商科(1理工科 2理科 3商科)")
    private Integer artsScience;

    @ApiModelProperty("专业ID")
    private Long professionalId;

    @ApiModelProperty("专业名称")
    private String professionalName;

    @ApiModelProperty("班主任用户编码")
    private Long headTeacher;

    @ApiModelProperty("班主任名称")
    private String headTeacherName;

    @ApiModelProperty("sid")
    private String sid;

//    @ApiModelProperty("创建时间")
//    private LocalDateTime createTime;
//
//    @ApiModelProperty("更新时间")
//    private LocalDateTime updateTime;
//
//    @ApiModelProperty("是否删除 (0. 否，1. 是)")
//    private Integer isDeleted;
    @ApiModelProperty("是否升级 (0. 否，1. 是)")
    private Integer upgrade;

    // 增加 schoolId 字段
    @ApiModelProperty("学校ID")
    private Long schoolId;
}