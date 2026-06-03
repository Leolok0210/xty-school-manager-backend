package com.xiaotiyun.school.manager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.xiaotiyun.school.manager.basic.enums.SchoolLanguageEnum;
import com.xiaotiyun.school.manager.dao.SysLanguageDao;
import com.xiaotiyun.school.manager.model.entity.SysLanguageEntity;
import com.xiaotiyun.school.manager.service.SysLanguageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.concurrent.TimeUnit;

/**
 * 国际化资源Service实现类
 */
@Slf4j
@Service
public class SysLanguageServiceImpl extends ServiceImpl<SysLanguageDao, SysLanguageEntity> implements SysLanguageService {
    // 本地缓存（code+language 作为键）
    private final Cache<String, String> messageCache = Caffeine.newBuilder()
            .maximumSize(1000) // 最大缓存条目
            .expireAfterWrite(60, TimeUnit.SECONDS)
            .initialCapacity(100)
            .build();

    @Override
    public String getMessage(String code, String language) {
        if (ObjectUtils.isEmpty(code)) {
            return "";
        }

        String cacheKey = buildCacheKey(code, language);

        // 优先从缓存获取
        return messageCache.get(cacheKey, k ->
                queryFromDatabase(code, language)
        );
    }

    private String queryFromDatabase(String code, String language) {
        log.debug("从数据库查询国际化资源，code：{}，language：{}", code, language);
        LambdaQueryWrapper<SysLanguageEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysLanguageEntity::getCode, code)
                .eq(SysLanguageEntity::getLanguage, language);

        SysLanguageEntity i18n = getOne(wrapper);
        if (i18n == null) {
            // 尝试默认语言
            wrapper.clear();
            wrapper.eq(SysLanguageEntity::getCode, code)
                    .eq(SysLanguageEntity::getLanguage, SchoolLanguageEnum.ZH_MO.getCode());
            i18n = getOne(wrapper);
        }
        return i18n != null ? i18n.getContent() : code;
    }

    private String buildCacheKey(String code, String language) {
        return code + ":" + language;
    }
} 