package com.xiaotiyun.school.manager.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class LeisureActivitiesNoticeSendDTO {
    /**
     * 学校id
     */
    private Long schoolId;
    /**
     * 活动id
     */
    private Long activityId;
    /**
     * 学段id
     */
    private Long periodId;
    /**
     * 公布结果信息
     */
    private List<LeisureActivitiesNoticeSendStudentDTO> publishResults;
}
