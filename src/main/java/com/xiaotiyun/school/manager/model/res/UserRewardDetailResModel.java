package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@ApiModel("奖励详情返回信息")
public class UserRewardDetailResModel {
    @ApiModelProperty("ID")
    private Long id;

    @ApiModelProperty("所属学年")
    private String sid; // 修改: Long 改为 String

    @ApiModelProperty("学校ID")
    private Long schoolId;

    @ApiModelProperty("学期")
    private Long term;

    @ApiModelProperty("学段name")
    private String termName;

    @ApiModelProperty("日期")
    private LocalDateTime date;

    @ApiModelProperty("学生id")
    private Long studentId;

    @ApiModelProperty("学生座位号")
    private Integer seatNo;

    @ApiModelProperty("学生姓名")
    private String studentName;

    @ApiModelProperty("班级name")
    private String className;

    @ApiModelProperty("班级number")
    private Integer classNumber;

    @ApiModelProperty("类型 1奖励 2惩罚")
    private Integer type;

    @ApiModelProperty("奖励原因")
    private String rewardReason;

    @ApiModelProperty("大功/大过")
    private Integer maxReward;

    @ApiModelProperty("中功/小过")
    private Integer midReward;

    @ApiModelProperty("优点/缺点")
    private Integer minReward;

    @ApiModelProperty("组级name")
    public String gradeGroupName;

    @ApiModelProperty("班级id")
    private Long classId;

    @ApiModelProperty("是否自动设置,0-否，1-是")
    private Integer isAuto;

    @ApiModelProperty("自动计算类型，0-课堂表现，1-欠交作业，2-仪表不符，3-迟到次数,4-欠课本")
    private Integer autoType;

    @ApiModelProperty("更新时间")
    private LocalDateTime updateTime;
}