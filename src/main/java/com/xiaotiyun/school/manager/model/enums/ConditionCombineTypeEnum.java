package com.xiaotiyun.school.manager.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ConditionCombineTypeEnum {
    AND("AND", "且"),
    OR("OR", "或");
    
    private final String code;
    private final String desc;
    
    public static ConditionCombineTypeEnum getByCode(String code) {
        if (code == null) {
            return null;
        }
        for (ConditionCombineTypeEnum value : values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return null;
    }
} 