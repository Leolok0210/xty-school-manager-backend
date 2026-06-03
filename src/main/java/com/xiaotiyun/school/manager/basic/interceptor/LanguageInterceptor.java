package com.xiaotiyun.school.manager.basic.interceptor;


import com.xiaotiyun.school.manager.basic.util.LanguageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 语言拦截器
 */
@Component
public class LanguageInterceptor implements HandlerInterceptor {

    private final LanguageUtil languageUtil;

    @Autowired
    public LanguageInterceptor(LanguageUtil languageUtil) {
        this.languageUtil = languageUtil;
    }
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String language = request.getHeader("Accept-Language");
        languageUtil.setLanguage(language);
        return true;
    }
    
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        languageUtil.clearLanguage();
    }
} 