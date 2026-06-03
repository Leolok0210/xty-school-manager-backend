package com.xiaotiyun.school.manager.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * AI对话学习记录表
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("ai_conversation_learn")
public class AiConversationLearnEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 学校ID
     */
    private Long schoolId;

    /**
     * 用户原始问题
     */
    private String userQuery;

    /**
     * 标准化后的问题
     */
    private String normalizedQuery;

    /**
     * AI回复内容
     */
    private String aiResponse;

    /**
     * 出現次数
     */
    private Integer queryCount;

    /**
     * 正向反馈次数
     */
    private Integer positiveCount;

    /**
     * 负向反馈次数
     */
    private Integer negativeCount;

    /**
     * 是否已纳入知识库
     */
    private Boolean isLearned;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT, value = "created_at")
    private java.util.Date createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE, value = "updated_at")
    private java.util.Date updateTime;

    /**
     * 是否删除
     */
    @TableLogic
    private Boolean deleted;
}