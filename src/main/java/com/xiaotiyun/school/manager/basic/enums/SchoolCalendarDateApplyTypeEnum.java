package com.xiaotiyun.school.manager.basic.enums;

import lombok.Getter;

public enum SchoolCalendarDateApplyTypeEnum {
    TEACHER(1, "适用老师"),
    STUDENT(2, "适用学生");

    @Getter
    private final int code;
    @Getter
    private final String value;

    SchoolCalendarDateApplyTypeEnum(int code, String value) {
        this.code = code;
        this.value = value;
    }
}
