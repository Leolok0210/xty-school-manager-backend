package com.xiaotiyun.school.manager.model.dto;

import lombok.Data;

@Data
public class StudentLateRecordDTO {
    private Long studentId; // 学生ID
    private Integer lateCount; // 迟到
} 