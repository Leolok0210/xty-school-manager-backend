package com.xiaotiyun.school.manager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiaotiyun.school.manager.model.entity.SysLanguageEntity;


/**
 * 国际化资源Service
 */
public interface SysLanguageService extends IService<SysLanguageEntity> {

    /**
     * 获取国际化消息
     */
    String getMessage(String code, String language);
} 