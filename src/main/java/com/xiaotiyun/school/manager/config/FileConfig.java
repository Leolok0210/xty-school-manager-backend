package com.xiaotiyun.school.manager.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "file")
public class FileConfig {
    /**
     * 文件保存根文件目录
     */
    private String fileRootPath;
    /**
     * 最大照片尺寸
     */
    private Long maxFileSize;
    /**
     * 最小照片尺寸
     */
    private Long minFileSize;

    /**
     * 最大学生请假文件尺寸
     */
    private Long maxLeaveFileSize;

    /**
     * 文件前缀
     */
    private String filePrefix;

    private String templateUrl;
} 