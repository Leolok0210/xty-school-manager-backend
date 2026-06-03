package com.xiaotiyun.school.manager.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CommentTemplateVarEnum {
    CHINESE_NAME("1", "中文姓名"),
    ENGLISH_NAME("2", "英文姓名");
    
    private final String code;
    private final String desc;
    
    public static CommentTemplateVarEnum getByCode(String code) {
        if (code == null) {
            return null;
        }
        for (CommentTemplateVarEnum value : values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return null;
    }
} 