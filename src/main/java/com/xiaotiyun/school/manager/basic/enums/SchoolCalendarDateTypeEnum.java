package com.xiaotiyun.school.manager.basic.enums;

import lombok.Getter;

public enum SchoolCalendarDateTypeEnum {
    WEEKDAY(1, "工作日"),
    WEEKEND(2, "双休日"),
    HOLIDAY(3, "假期");

    @Getter
    private final int code;
    @Getter
    private final String value;

    SchoolCalendarDateTypeEnum(int code, String value) {
        this.code = code;
        this.value = value;
    }
}
