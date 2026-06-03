package com.xiaotiyun.school.manager.basic.enums;

import java.util.Arrays;

public enum ActProcessTemplateTypeEnum {
    TEACHER_LEAVE(1, "教师请假"),
    TEACHER_BUSINESS(2, "教师公务"),
    STUDENT_REWARD_PUNISHMENT(3, "学生奖惩审批");

    private final Integer code;
    private final String desc;

    ActProcessTemplateTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static ActProcessTemplateTypeEnum getByCode(Integer code) {
        return Arrays.stream(values())
            .filter(e -> e.code.equals(code))
            .findFirst()
            .orElse(null);
    }
}
