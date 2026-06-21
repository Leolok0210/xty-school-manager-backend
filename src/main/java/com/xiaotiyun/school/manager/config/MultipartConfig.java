package com.xiaotiyun.school.manager.config;

import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;

import javax.servlet.MultipartConfigElement;

/**
 * 显式配置 multipart 上传上限。
 * 仅靠 application-prod.yml 的 spring.servlet.multipart 在线上未生效（实测生效的是 Spring 默认 1MB），
 * 这里用代码注册 MultipartConfigElement 强制覆盖，保证大文件（校园风采图片/视频）可上传。
 */
@Configuration
public class MultipartConfig {

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setMaxFileSize(DataSize.ofMegabytes(200));
        factory.setMaxRequestSize(DataSize.ofMegabytes(200));
        return factory.createMultipartConfig();
    }
}
