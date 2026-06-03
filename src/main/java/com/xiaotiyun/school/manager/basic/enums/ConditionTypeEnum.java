package com.xiaotiyun.school.manager.basic.enums;

import java.util.Arrays;

public enum ConditionTypeEnum {
    APPLICANT(1, "发起人条件"),
    DURATION(2, "时长条件"),
    LEAVE_TYPE(3, "请假类型");

    private final Integer code;
    private final String desc;

    ConditionTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static ConditionTypeEnum getByCode(Integer code) {
        return Arrays.stream(values())
            .filter(e -> e.code.equals(code))
            .findFirst()
            .orElse(null);
    }
}
