package com.xiaotiyun.school.manager.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaotiyun.school.manager.basic.common.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("quality_evaluation_comment_rule")
public class QualityEvaluationCommentRuleEntity extends BaseEntity {
    
    /**
     * 学校ID
     */
    private Long schoolId;
    
    /**
     * 规则名称
     */
    private String ruleName;
    
    /**
     * 优先级
     */
    private Integer priority;
    
    /**
     * 条件设置(JSON格式)
     */
    private String conditions;
    
    /**
     * 评语模板
     */
    private String commentTemplate;

    /**
     * 评语状态0开启，1关闭
     */
    private Integer status;
} 