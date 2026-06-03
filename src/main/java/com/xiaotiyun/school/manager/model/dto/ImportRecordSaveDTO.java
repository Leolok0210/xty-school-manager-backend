package com.xiaotiyun.school.manager.model.dto;

import lombok.Data;

@Data
public class ImportRecordSaveDTO {

    /**
     * 任务id
     */
    private Long taskId;

    /**
     * 错误行号/照片名称
     */
    private String incorrectLineno;

    /**
     * 错误原因
     */
    private String incorrectReason;
}