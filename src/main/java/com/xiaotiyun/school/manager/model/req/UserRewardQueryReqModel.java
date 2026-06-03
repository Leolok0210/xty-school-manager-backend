package com.xiaotiyun.school.manager.model.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("奖励查询请求信息")
public class UserRewardQueryReqModel {
    @ApiModelProperty(value = "页码",required = true)
    private Integer pageNum;

    @ApiModelProperty(value = "每页大小",required = true)
    private Integer pageSize;

    @ApiModelProperty(value = "所属学年",required = true)
    private String sid; // 修改: Long 改为 String

    @ApiModelProperty(value = "学校ID",required = true)
    private Long schoolId;

    @ApiModelProperty(value = "学期",required = true)
    private Long term;
    @ApiModelProperty("学部(1:幼稚园 2:小学 3:中学)")
    private Integer department;
    @ApiModelProperty("学生id")
    private Long studentId;

    @ApiModelProperty(value = "类型 1奖励 2惩罚",required = true)
    private Integer type;

    @ApiModelProperty("学生name")
    private String studentName;

    @ApiModelProperty("classId")
    private String classId;

    @ApiModelProperty("是否自动设置")
    private Integer isAuto;

    @ApiModelProperty("自动计算类型，0-课堂表现，1-欠交作业，2-仪表不符，3-迟到次数")
    private Integer autoType;

    private Long userId;


    private List<Long> classIds;

}