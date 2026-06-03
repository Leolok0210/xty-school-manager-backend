package com.xiaotiyun.school.manager.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 流程实例节点快照表实体类
 * 对应数据库表：act_instance_node
 */
@Data
@TableName("act_instance_node")
public class ActInstanceNodeEntity {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 流程实例ID
     */
    private Long instanceId;

    /**
     * 节点类型（1.发起人;2.审批人;3.抄送人;4.条件分支）
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
     * 来源节点(保存的为上一个节点code)
     */
    private String nodeFrom;

    /**
     * 审批类型（1.人工审批；2.自动通过）
     */
    private Integer approverType;

    /**
     * 审批人id集合(JSON格式)
     */
    private String approverIds;

    /**
     * 多人审批方式（1.或签;2.会签）
     */
    private Integer multiApproveMode;
}