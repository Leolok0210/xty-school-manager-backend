package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("级组详细信息响应模型")
public class GradeGroupDetailResModel {
    @ApiModelProperty("级组ID")
    private Long id;

    @ApiModelProperty("学部（1:幼稚园 2:小学 3:中学）")
    private Long department;

    @ApiModelProperty("级组名称")
    private String gradeGroupName;

    @ApiModelProperty("学校ID")
    private Long schoolId;

    @ApiModelProperty("专业分科：0-不分科；1-专业分科；2-文理分科 3-理工商分科")
    private Integer professionalSubject;

    @ApiModelProperty("文理分科类型：1-按班级分科；2-按学生分科")
    private Integer artsScienceType;

    @ApiModelProperty("级组")
    private String grade;

    @ApiModelProperty("级组下的班级列表 查询全部级组列表时不返回")
    private List<SysClassListResModel> list;
}