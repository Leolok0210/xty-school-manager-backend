package com.xiaotiyun.school.manager.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaotiyun.school.manager.basic.common.BaseEntity;
import lombok.Data;

/**
 * 审批流程节点表
 */
@Data
@TableName("act_process_node")
public class ActProcessNodeEntity extends BaseEntity {
    /**
     * 模板ID
     */
    private Long templateId;
    /**
     * 流程定义ID
     */
    private Long definitionId;
    /**
     * 节点类型（1.发起人;2.审批人;3.抄送人;4.条件分支;5.网关节点）
     */
    private Integer nodeType;
    /**
     * 节点code(本流程中唯一)
     */
    private String nodeCode;
    /**
     * 节点名称
     */
    private String nodeName;
    /**
     * 节点展示名称
     */
    private String nodeDisplayName;
    /**
     * 来源节点(保存的为上一个节点code)
     */
    private String nodeFrom;
    /**
     * 审批类型（1.人工审批；2.自动通过）
     */
    private Integer approverType;
    /**
     * 节点配置(JSON格式)
     */
    private String config;
    /**
     * 节点优先级（同一流程内顺序）
     */
    private Integer priority;
    /**
     * 多人审批方式（1.或签;2.会签）
     */
    private Integer multiApproveMode;
}