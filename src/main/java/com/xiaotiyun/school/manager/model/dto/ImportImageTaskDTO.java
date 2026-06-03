package com.xiaotiyun.school.manager.model.dto;

import com.xiaotiyun.school.manager.basic.enums.SchoolLanguageEnum;
import lombok.Data;

@Data
public class ImportImageTaskDTO {
    /**
     * 任务id
     */
    private Long taskId;
    /**
     * 语言
     */
    private SchoolLanguageEnum languageEnum;
}