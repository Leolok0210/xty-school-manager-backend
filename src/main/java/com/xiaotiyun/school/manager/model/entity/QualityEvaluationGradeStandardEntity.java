package com.xiaotiyun.school.manager.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaotiyun.school.manager.basic.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("quality_evaluation_grade_standard")
public class QualityEvaluationGradeStandardEntity extends BaseEntity {
    
    /**
     * 学校ID
     */
    private Long schoolId;

    /**
     * 学部(1:幼稚园 2:小学 3:中学)
     */
    private Integer department;
    
    /**
     * 评价等级
     */
    private String grade;
    
    /**
     * 分数区间最小值
     */
    private Integer scoreMin;
    
    /**
     * 分数区间最大值
     */
    private Integer scoreMax;
} 