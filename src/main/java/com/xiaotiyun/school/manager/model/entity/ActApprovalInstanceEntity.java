package com.xiaotiyun.school.manager.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaotiyun.school.manager.basic.common.BaseEntity;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 审批实例表
 */
@Data
@TableName("act_approval_instance")
public class ActApprovalInstanceEntity extends BaseEntity {
    /**
     * 业务ID（关联具体业务数据）
     */
    private Long businessId;
    /**
     * 流程定义ID
     */
    private Long definitionId;
    /**
     * 审批类型(1.教师请假；2.教师公务)
     */
    private Integer processType;
    /**
     * 审批标题
     */
    private String title;
    /**
     * 状态：0-草稿，1-运行中，2-已完成，3-已拒绝，4-已撤销
     */
    private Integer status;
    /**
     * 发起人ID
     */
    private Long startUserId;
    /**
     * 发起人姓名
     */
    private String startUserName;
    /**
     * 发起时间
     */
    private LocalDateTime startTime;
    /**
     * 结束时间
     */
    private LocalDateTime endTime;
}