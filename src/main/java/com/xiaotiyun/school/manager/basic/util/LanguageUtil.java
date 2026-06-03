package com.xiaotiyun.school.manager.basic.util;


import com.xiaotiyun.school.manager.basic.enums.SchoolLanguageEnum;
import com.xiaotiyun.school.manager.service.SysLanguageService;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;

/**
 * 国际化工具类
 */
@Component
public class LanguageUtil {

    @Resource
    private SysLanguageService sysLanguageService;

    /**
     * 获取国际化消息
     */
    public String getMessage(String code) {
        return sysLanguageService.getMessage(code, getCurrentLanguage());
    }

    /**
     * 获取国际化消息
     */
    public String getMessage(String code, Object... args) {
        String message = getMessage(code);
        return String.format(message, args);
    }

    /**
     * 设置语言
     */
    public void setLanguage(String language) {
        LocaleContextHolder.setLocale(java.util.Locale.forLanguageTag(SchoolLanguageEnum.getDefValue(language).getCode()));
    }

    /**
     * 获取当前语言
     */
    public static String getCurrentLanguage() {
        return LocaleContextHolder.getLocale().toLanguageTag();
    }

    /**
     * 清除语言
     */
    public static void clearLanguage() {
        LocaleContextHolder.resetLocaleContext();
    }
} 