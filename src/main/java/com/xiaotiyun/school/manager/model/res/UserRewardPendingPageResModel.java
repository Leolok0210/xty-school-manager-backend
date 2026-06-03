package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class UserRewardPendingPageResModel {
    @ApiModelProperty(value = "奖惩id")
    private Long id;
    @ApiModelProperty(value = "任务id")
    private Long taskId;
    @ApiModelProperty(value = "实例id")
    private Long instanceId;
    @ApiModelProperty(value = "学段")
    private String termName;
    @ApiModelProperty(value = "会议通过日期")
    private LocalDate meetingDate;
    @ApiModelProperty(value = "级组名称")
    private String groupName;
    @ApiModelProperty(value = "班级名称")
    private String className;
    @ApiModelProperty(value = "学生姓名")
    private String studentName;
    @ApiModelProperty(value = "原因")
    private String rewardReason;
    @ApiModelProperty(value = "类型(1.大过;2.小过;3.缺点;4.大功;5.小功;6.优点)")
    private Integer type;
    @ApiModelProperty(value = "次数")
    private Integer frequency;
    @ApiModelProperty(value = "状态(1.待审批；2.已完成；3.已拒绝；4.已撤回)")
    private Integer status;
    @ApiModelProperty(value = "审批意见")
    private String comment;
    @ApiModelProperty(value = "创建时间")
    private LocalDateTime startTime;
    @ApiModelProperty(value = "创建人")
    private String startUserName;
}