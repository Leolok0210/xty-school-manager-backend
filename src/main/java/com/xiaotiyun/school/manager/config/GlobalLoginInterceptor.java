package com.xiaotiyun.school.manager.config;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 全局登錄攔截器：除 @SaIgnore 標記的方法外，所有請求必須登錄
 */
public class GlobalLoginInterceptor implements HandlerInterceptor {

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
}
