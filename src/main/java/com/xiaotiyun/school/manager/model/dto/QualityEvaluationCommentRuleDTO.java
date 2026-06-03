package com.xiaotiyun.school.manager.model.dto;


import lombok.Data;


import java.util.List;

@Data
public class QualityEvaluationCommentRuleDTO {
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


    private List<ConditionGroupDTO> conditions;

    /**
     * 评语模板
     */
    private String commentTemplate;
}
