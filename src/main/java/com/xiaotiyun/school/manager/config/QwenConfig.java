package com.xiaotiyun.school.manager.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "qwen")
public class QwenConfig {
    private String apiKey;
    private String apiUrl;
    private String model;
    private Integer timeout = 30000;
}