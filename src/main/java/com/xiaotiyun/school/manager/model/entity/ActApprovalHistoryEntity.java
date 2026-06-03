package com.xiaotiyun.school.manager.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaotiyun.school.manager.basic.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 审批历史表实体类
 * 对应数据库表：act_approval_history
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("act_approval_history")
public class ActApprovalHistoryEntity extends BaseEntity {

    /**
     * 实例ID
     */
    private Long instanceId;

    /**
     * 任务ID
     */
    private Long taskId;

    /**
     * 节点code
     */
    private String nodeCode;

    /**
     * 审批结果：1-同意，2-拒绝
     */
    private Integer approvalResult;

    /**
     * 操作人ID
     */
    private Long operateUserId;

    /**
     * 操作人姓名
     */
    private String operateUserName;

    /**
     * 审批意见
     */
    private String comment;

    /**
     * 操作时间
     */
    private LocalDateTime operateTime;
}