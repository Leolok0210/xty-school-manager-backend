package com.xiaotiyun.school.manager.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaotiyun.school.manager.basic.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 国际化资源实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_language")
public class SysLanguageEntity extends BaseEntity {

    /**
     * 国际化编码
     */
    private String code;

    /**
     * 语言编码
     */
    private String language;

    /**
     * 翻译内容
     */
    private String content;
} 