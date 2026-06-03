package com.xiaotiyun.school.manager.model.dto;

import lombok.Data;

@Data
public class ExportHeaderDTO {
    /**
     * excel表头名称
     */
    private String name;
    /**
     * 数据字段
     */
    private String field;
}