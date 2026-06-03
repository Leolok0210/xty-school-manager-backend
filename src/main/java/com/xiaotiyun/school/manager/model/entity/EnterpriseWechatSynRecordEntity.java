package com.xiaotiyun.school.manager.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.xiaotiyun.school.manager.basic.common.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 企业微信关联错误信息记录表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("enterprise_wechat_syn_record")
public class EnterpriseWechatSynRecordEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;


    /**
     * 任务id
     */

    @TableField("task_id")
    private Long taskId;

    /**
     * 关联id
     */

    @TableField("rel_id")
    private Long relId;

    /**
     * 错误原因
     */

    @TableField("incorrect_reason")
    private String incorrectReason;


}