package com.xiaotiyun.school.manager.model.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel("学生素质评分查询请求信息")
public class StudentQualityScoreQueryReqModel {
    @ApiModelProperty(value = "页码",required = true)
    private Integer pageNum;

    @ApiModelProperty(value = "每页大小",required = true)
    private Integer pageSize;

    @ApiModelProperty(value = "学年 素质评分接口必须传入",required = true)
    private String sid;

    @ApiModelProperty(value = "学段 素质评分接口必须传入",required = true)
    private Long term;
//
//    @ApiModelProperty("学生姓名")
//    private String studentName;

    @NotNull(message = "班级ID不能为空")
    @ApiModelProperty(value = "班级",required = true)
    private Long classId;

    @NotNull(message = "学校ID不能为空")
    @ApiModelProperty(value = "学校ID",required = true)
    private Long schoolId;


    private Long userId;

}