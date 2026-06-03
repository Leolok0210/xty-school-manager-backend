package com.xiaotiyun.school.manager.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class PenaltyRuleDTO {

    /**
     * 处罚类型，具体含义：
     * 1(表示上课违规-次数)
     * 2(表示欠作业-次数)
     * 3(表示仪表不符-次数)
     * 4(表示迟到（入校+课堂）-次数)
     * 5(表示欠课本-次数)
     * 6(表示缺席-节数)
     * 7(表示欠回條-次)
     */
    private String type;
    /**
     * 出现的次数
     */
    private String frequency;
    /**
     * 处罚个数
     */
    private String quantity;
    /**
     * 处罚类型，具体含义：
     * 1. 大过
     * 2. 小过
     * 3. 缺点
     */
    private String penaltyType;


    /**
     * 特殊规则
     */
    private List<PenaltyRuleSpecialConfigDTO> specialConfigs;
}
