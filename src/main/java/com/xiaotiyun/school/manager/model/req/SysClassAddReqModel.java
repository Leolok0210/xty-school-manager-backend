package com.xiaotiyun.school.manager.model.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel("班级添加请求信息")
public class SysClassAddReqModel {

    @ApiModelProperty("ID")
    private Long id;
    @ApiModelProperty(value = "学部(1:幼稚园 2:小学 3:中学)",required = true)
    private Integer department;

    @NotNull(message = "级组不能为空")
    @ApiModelProperty(value = "级组",required = true)
    private Long gradeGroup;

    @NotNull(message = "级组不能为空")
    @ApiModelProperty(value = "级组name",required = true)
    private String gradeGroupName;

//    @NotNull(message = "班级序号不能为空")
//    @ApiModelProperty("班级序号")
//    private Integer classSerialNumber;

    @ApiModelProperty(value = "班级名称",required = true)
    private String className;

//    @NotNull(message = "是否专业班不能为空")
//    @ApiModelProperty(value = "是否专业班 (0. 否，1. 是)",required = true)
//    private Integer professionalVersion;

    @ApiModelProperty(value = "文理科(1文科、2理科) 理工商(1理科、2理工科、3商科)（级组设置为文理科，按班级区分时，必传）")
    private Integer artsScience;

    @ApiModelProperty("专业ID,（级组设置为专业分科时，必传）")
    private Long professionalId;

    @ApiModelProperty("班主任")
    private Long headTeacher;

    @NotNull(message = "sid不能为空")
    @ApiModelProperty(value = "学年 传入list的学年必须一致",required = true)
    private String sid;

}