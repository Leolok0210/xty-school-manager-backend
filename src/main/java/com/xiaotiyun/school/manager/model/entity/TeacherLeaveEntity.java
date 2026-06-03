package com.xiaotiyun.school.manager.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaotiyun.school.manager.basic.common.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("teacher_leave")
@ApiModel(value = "教师请假实体")
public class TeacherLeaveEntity extends BaseEntity {
    
    @ApiModelProperty(value = "学校ID", required = true)
    private Long schoolId;
    
    @ApiModelProperty(value = "教师ID", required = true)
    private Long teacherId;
    
    @ApiModelProperty(value = "请假类型", required = true)
    private Integer leaveType;

    @ApiModelProperty(value = "开始时间", required = true)
    private LocalDateTime startTime;
    
    @ApiModelProperty(value = "结束时间", required = true)
    private LocalDateTime endTime;
    
    @ApiModelProperty(value = "请假事由", required = true)
    private String reason;

    @ApiModelProperty("请假状态（0-待审批，1-审批通过，2-审批拒绝，3-已撤销）")
    private Integer leaveStatus;

    @ApiModelProperty("完成时间")
    private LocalDateTime completeTime;

    @ApiModelProperty("处理原因，拒绝时必填")
    private String handleOpinion;
}