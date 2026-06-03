package com.xiaotiyun.school.manager.model.res;

import lombok.Data;
import java.util.Map;

@Data
public class SystemSettingResModel {
    
    /**
     * 学校ID
     */
    private Long schoolId;
    
    /**
     * 所有配置项
     */
    private Map<String, String> settings;
}