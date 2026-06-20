package com.xiaotiyun.school.manager.config;

import cn.dev33.satoken.stp.StpUtil;
import org.springframework.core.annotation.Order;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 全局認證過濾器：除白名單路徑外，所有請求必須登錄
 */
@Order(1)
@WebFilter(urlPatterns = "/*")
public class AuthFilter implements Filter {

    private static final String[] WHITELIST = {
        "/api/auth/login",
        "/doc.html",
        "/webjars/",
        "/swagger-resources/",
        "/favicon.ico",
        "/health/check",
        "/v2/api-docs",
        "/v3/api-docs",
        "/api/transcript/details/class",
        "/api/device/",
        "/api/card/",
        "/api/face/",
        "/api/attendance/",
        "/api/campus-photos/",
    };

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        String path = request.getRequestURI();

        for (String white : WHITELIST) {
            if (path.startsWith(white)) {
                chain.doFilter(req, res);
                return;
            }
        }

        try {
            StpUtil.checkLogin();
        } catch (Exception e) {
            HttpServletResponse response = (HttpServletResponse) res;
            response.setStatus(401);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"error\":401,\"message\":\"暫未登錄或token已經過期\",\"data\":null}");
            return;
        }

        chain.doFilter(req, res);
    }

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void destroy() {
    }
}
