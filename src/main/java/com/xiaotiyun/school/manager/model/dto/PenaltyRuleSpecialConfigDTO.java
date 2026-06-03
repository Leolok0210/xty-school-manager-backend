package com.xiaotiyun.school.manager.model.dto;

import lombok.Data;

@Data
public class PenaltyRuleSpecialConfigDTO {

    /**
     * 次数（第几次）
     */
    private Integer times;
    /**
     * 出现的次数
     */
    private String frequency;
    /**
     * 处罚个数
     */
    private String quantity;
}
