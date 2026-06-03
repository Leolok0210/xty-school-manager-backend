package com.xiaotiyun.school.manager.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaotiyun.school.manager.basic.common.BaseEntity;
import lombok.Data;

/**
 * 审批流程模板表
 */
@Data
@TableName("act_process_template")
public class ActProcessTemplateEntity extends BaseEntity {
    /**
     * 学校ID
     */
    private Long schoolId;
    /**
     * 流程名称
     */
    private String processName;
    /**
     * 流程描述
     */
    private String processDesc;
    /**
     * 审批类型(1.教师请假；2.教师公务)
     */
    private Integer processType;
    /**
     * 发起人范围json
     */
    private String initiatorScope;
}