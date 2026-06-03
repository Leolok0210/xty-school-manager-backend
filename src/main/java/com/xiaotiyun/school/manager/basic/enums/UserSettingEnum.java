package com.xiaotiyun.school.manager.basic.enums;

import lombok.Getter;

@Getter
public enum UserSettingEnum {

    // 示例枚举常量
    LANGUAGE_TIME("language_time", "用户系统设置，语言和时间"),
    ;

    private String key;
    private String description;

    UserSettingEnum(String key, String description) {
        this.key = key;
        this.description = description;
    }
}
