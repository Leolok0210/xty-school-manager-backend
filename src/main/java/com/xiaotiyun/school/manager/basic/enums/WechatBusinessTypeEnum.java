package com.xiaotiyun.school.manager.basic.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum WechatBusinessTypeEnum {
    CREATE(1, "新增"),
    UPDATE(2, "更新");
    
    private final Integer code;
    private final String desc;

    public static WechatBusinessTypeEnum getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (WechatBusinessTypeEnum value : values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return null;
    }

    //getbydesc
    public static WechatBusinessTypeEnum getByDesc(String desc) {
        if (desc == null) {
            return null;
        }
        for (WechatBusinessTypeEnum value : values()) {
            if (value.getDesc().equals(desc)) {
                return value;
            }
        }
        return null;
    }
}