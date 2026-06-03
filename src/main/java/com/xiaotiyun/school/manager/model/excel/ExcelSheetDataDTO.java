package com.xiaotiyun.school.manager.model.excel;

import lombok.Data;
import lombok.Builder;
import java.util.List;

@Data
@Builder
public class ExcelSheetDataDTO {
    /**
     * sheet名称
     */
    private String sheetName;
    
    /**
     * 表头配置
     */
    private List<List<String>> headers;
    
    /**
     * 数据内容
     */
    private List<List<Object>> data;
} 