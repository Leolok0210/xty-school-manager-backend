package com.xiaotiyun.school.manager.model.dto;

import lombok.Data;

@Data
public class LeisureActivitiesRatingRangeDTO {
    /**
     * 最小值*100
     */
    private Integer minValue;
    /**
     * 最大值*100
     */
    private Integer maxValue;
    /**
     * 参考评分等级
     */
    private String level;
}
