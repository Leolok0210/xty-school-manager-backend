package com.xiaotiyun.school.manager.basic.enums;

import lombok.Getter;

public enum SchoolCalendarEventTypeEnum {
    TEACHER_HOLIDAY(1, "教师假期"),
    EXAM(2, "考试"),
    ACTIVITY(3, "活动"),
    OTHER(4, "其他"),
    STUDENT_HOLIDAY(5, "学生假期");

    @Getter
    private final int code;
    @Getter
    private final String value;

    SchoolCalendarEventTypeEnum(int code, String value) {
        this.code = code;
        this.value = value;
    }
}
