package com.xiaotiyun.school.manager.model.dto;

import lombok.Data;

@Data
public class StudentGraduateExamPartakeCountDTO {
    /**
     * 任务id
     */
    private Long taskId;
    /**
     * 参与人数
     */
    private Integer partakeCount;
}
