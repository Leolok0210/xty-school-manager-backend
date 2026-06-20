package com.xiaotiyun.school.manager.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SaTokenConfig implements WebMvcConfigurer {

    @Value("${spring.profiles.active:dev}")
    private String activeProfile;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        if ("dev".equals(activeProfile)) {
            return;
        }

        // 處理 @SaCheckPermission / @SaCheckRole 等註解
        registry.addInterceptor(new SaInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/doc.html",
                        "/webjars/**",
                        "/swagger-resources/**",
                        "/favicon.ico",
                        "/health/check",
                        "/api/transcript/details/class",
                        "/api/device/**",
                        "/api/attendance/**",
                        "/api/manage/device-attendance/**",
                        "/api/campus-photos/**",
                        "/v2/api-docs/**",
                        "/v3/api-docs/**");
    }
}
