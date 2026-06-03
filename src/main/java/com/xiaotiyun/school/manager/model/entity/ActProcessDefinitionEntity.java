package com.xiaotiyun.school.manager.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaotiyun.school.manager.basic.common.BaseEntity;
import lombok.Data;

/**
 * 审批流程定义表
 */
@Data
@TableName("act_process_definition")
public class ActProcessDefinitionEntity extends BaseEntity {
    /**
     * 模板ID
     */
    private Long templateId;
    /**
     * 规则设置（1.仅首个节点需审批；2.每个节点都需审批；3.仅连续审批自动同意）
     */
    private Integer ruleSetting;
    /**
     * 是否激活
     */
    private Boolean isActive;
}