package com.xiaotiyun.school.manager.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SaTokenConfig implements WebMvcConfigurer {
    
    @Value("${spring.profiles.active:dev}")
    private String activeProfile;
    
    // 注册Sa-Token的拦截器
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 如果是dev环境，则不进行权限校验
        if ("dev".equals(activeProfile)) {
            return;
        }
        // 注册路由拦截器，自定义认证规则
        registry.addInterceptor(new SaInterceptor(handle -> StpUtil.checkLogin()))
                .addPathPatterns("/**")
                .excludePathPatterns("/doc.html"
                        ,"/webjars/**"
                        ,"/swagger-resources/**"
                        ,"/favicon.ico"
                        ,"/health/check"
                        ,"/api/transcript/details/class"
                        ,"/v2/api-docs/**","/v3/api-docs/**");

    }
} 