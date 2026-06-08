package com.xiaotiyun.school.manager.config;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Configuration
public class SaTokenConfig implements WebMvcConfigurer {

    @Value("${spring.profiles.active:dev}")
    private String activeProfile;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        if ("dev".equals(activeProfile)) {
            return;
        }

        // 1. 全局登錄攔截器：除 @SaIgnore 外，所有請求必須登錄
        registry.addInterceptor(new HandlerInterceptor() {
            @Override
            public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                                     Object handler) {
                if (handler instanceof HandlerMethod) {
                    HandlerMethod hm = (HandlerMethod) handler;
                    if (hm.hasMethodAnnotation(SaIgnore.class) ||
                        hm.getBeanType().isAnnotationPresent(SaIgnore.class)) {
                        return true;
                    }
                }
                StpUtil.checkLogin();
                return true;
            }
        })
        .addPathPatterns("/**")
        .excludePathPatterns("/doc.html",
                "/webjars/**",
                "/swagger-resources/**",
                "/favicon.ico",
                "/health/check",
                "/api/transcript/details/class",
                "/v2/api-docs/**",
                "/v3/api-docs/**");

        // 2. Sa-Token 攔截器：處理 @SaCheckPermission / @SaCheckRole 等註解
        registry.addInterceptor(new SaInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/doc.html",
                        "/webjars/**",
                        "/swagger-resources/**",
                        "/favicon.ico",
                        "/health/check",
                        "/api/transcript/details/class",
                        "/v2/api-docs/**",
                        "/v3/api-docs/**");
    }
}
