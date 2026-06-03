package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@ApiModel("专业详情返回信息")
public class SchoolMajorDetailResModel {
    @ApiModelProperty("ID")
    private Long id;

//    @ApiModelProperty("所属学年")
//    private String sid;

    @ApiModelProperty("专业名称")
    private String majorName;

    @ApiModelProperty("学部(1:幼稚园 2:小学 3:中学)")
    private Integer department;

    @ApiModelProperty("专业科目列表")
    private List<SubjectSimpleResModel> majorSubjects;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty("更新时间")
    private LocalDateTime updateTime;
}