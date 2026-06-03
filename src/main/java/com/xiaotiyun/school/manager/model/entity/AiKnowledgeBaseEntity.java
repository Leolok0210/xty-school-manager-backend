package com.xiaotiyun.school.manager.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * AI知识库表
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("ai_knowledge_base")
public class AiKnowledgeBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 学校ID
     */
    private Long schoolId;

    /**
     * 分类: faq, process, notice, other
     */
    private String category;

    /**
     * 问题/关键词
     */
    private String question;

    /**
     * 答案内容
     */
    private String answer;

    /**
     * 排序号
     */
    private Integer sort;

    /**
     * 状态: 0禁用, 1启用
     */
    private Integer status;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    /**
     * 是否删除
     */
    @TableLogic
    private Boolean deleted;
}