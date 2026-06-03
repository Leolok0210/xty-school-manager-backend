package com.xiaotiyun.school.manager.model.dto;

import lombok.Data;

@Data
public class LeisureActivitiesNoticeSendStudentDTO {
    /**
     * 学生id
     */
    private Long studentId;
    /**
     * 匹配课程id(未匹配上不传)
     */
    private Long courseId;
}
