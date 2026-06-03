package com.xiaotiyun.school.manager.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaotiyun.school.manager.basic.common.BaseEntity;
import lombok.Data;

/**
 * 审批任务
 */
@Data
@TableName("act_approval_task")
public class ActApprovalTaskEntity extends BaseEntity {
    /**
     * 审批实例ID
     */
    private Long instanceId;
    /**
     * 节点code
     */
    private String nodeCode;
    /**
     * 节点名称
     */
    private String nodeName;
    /**
     * 审批人信息json
     */
    private String approverIds;
    /**
     * 状态：0-待处理，1-已处理，2-已取消
     */
    private Integer status;
}