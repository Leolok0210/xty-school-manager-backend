package com.xiaotiyun.school.manager.config;


import com.xiaotiyun.school.manager.basic.interceptor.LanguageInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC配置
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final LanguageInterceptor languageInterceptor;
    @Autowired
    public WebMvcConfig(LanguageInterceptor languageInterceptor) {
        this.languageInterceptor = languageInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 添加语言拦截器，应用到所有请求
        registry.addInterceptor(languageInterceptor).addPathPatterns("/**");
    }
} 