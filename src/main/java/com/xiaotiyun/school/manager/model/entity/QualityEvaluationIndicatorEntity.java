package com.xiaotiyun.school.manager.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaotiyun.school.manager.basic.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("quality_evaluation_indicator")
public class QualityEvaluationIndicatorEntity extends BaseEntity {
    
    /**
     * 学校ID
     */
    private Long schoolId;
    
    /**
     * 学部(1:幼稚园 2:小学 3:中学)
     */
    private Integer department;
    
    /**
     * 评价指标内容
     */
    private String content;
    
    /**
     * 权重(百分比)
     */
    private Integer weight;
    
    /**
     * 展示规则(SCORE-分数,GRADE-评级)
     */
    private String displayType;
} 