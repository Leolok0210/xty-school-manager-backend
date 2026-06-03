package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TeacherLeavePageResModel {
    @ApiModelProperty("id")
    private Long id;
    @ApiModelProperty("教师id")
    private Long teacherId;
    @ApiModelProperty("教师名称")
    private String teacherName;
    @ApiModelProperty("请假类型（1-事假，2-病假，3-年假，4-产假，5-陪产假，6-婚假，7-丧假，8-产检假，9-育儿假）")
    private Integer leaveType;
    @ApiModelProperty("开始时间")
    private LocalDateTime startTime;
    @ApiModelProperty("结束时间")
    private LocalDateTime endTime;
    @ApiModelProperty("请假事由")
    private String reason;

    @ApiModelProperty("请假状态（0-待审批，1-审批通过，2-审批拒绝，3-已撤销）")
    private Integer leaveStatus;

    @ApiModelProperty("处理原因，拒绝时必填")
    private String handleOpinion;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty("完成时间")
    private LocalDateTime completeTime;
}