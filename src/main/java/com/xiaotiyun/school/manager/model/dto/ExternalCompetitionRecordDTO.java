package com.xiaotiyun.school.manager.model.dto;

import lombok.Data;

@Data
public class ExternalCompetitionRecordDTO {
    private Long studentId;
    private Long competitionId;
    private String prize;
}
