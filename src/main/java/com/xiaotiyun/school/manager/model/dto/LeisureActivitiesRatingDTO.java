package com.xiaotiyun.school.manager.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class LeisureActivitiesRatingDTO {
    /**
     * 学部id
     */
    private Integer department;
    /**
     * 出勤率占比
     */
    private Integer attendanceRatio;
    /**
     * 课堂表现占比
     */
    private Integer classParticipationRatio;
    /**
     * 分数区间
     */
    private List<LeisureActivitiesRatingRangeDTO> scoreRange;
}
