package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class UserRewardInfoResModel {
    @ApiModelProperty(value = "奖励类型,0-常规类型,1-竞赛奖励类型", required = true)
    private Integer registerType;
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
    @ApiModelProperty(value = "备注")
    private String remark;
    @ApiModelProperty(value = "状态(1.待审批；2.已完成；3.已拒绝；4.已撤回)")
    private Integer status;
    @ApiModelProperty(value = "发起时间")
    private LocalDateTime initiatedStartTime;
    @ApiModelProperty(value = "发起人")
    private String startUserName;
    @ApiModelProperty(value = "表彰建议")
    private String awardsRemark;
    @ApiModelProperty(value = "审批备注")
    private String approveRemark;
    @ApiModelProperty(value = "节点信息")
    private List<ActApprovalInstanceInfoNodeResModel> nodes;
}