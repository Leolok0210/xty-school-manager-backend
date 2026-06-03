package com.xiaotiyun.school.manager.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaotiyun.school.manager.basic.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 审批抄送记录表实体类
 * 对应数据库表：act_approval_cc
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("act_approval_cc")
public class ActApprovalCcEntity extends BaseEntity {

    /**
     * 审批实例ID
     */
    private Long instanceId;

    /**
     * 节点code
     */
    private String nodeCode;

    /**
     * 抄送用户ID
     */
    private Long ccUserId;

    /**
     * 抄送用户姓名
     */
    private String ccUserName;

    /**
     * 抄送时间
     */
    private LocalDateTime ccTime;
}