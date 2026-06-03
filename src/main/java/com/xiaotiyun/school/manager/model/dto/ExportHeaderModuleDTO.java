package com.xiaotiyun.school.manager.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class ExportHeaderModuleDTO {
    /**
     * 模块:basic基础数据、family家庭情况、enrollment入学记录、medicalNotice医护注意事项、crossBorder跨境学生、dateRecord日期记录
     */
    private String module;
    /**
     * 表头信息
     */
    private List<ExportHeaderDTO> headers;
}