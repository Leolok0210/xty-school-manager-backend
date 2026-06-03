package com.xiaotiyun.school.manager.basic.enums;

import java.util.Arrays;

public enum ActApprovalTaskStatusEnum {
    PENDING(0, "待处理"),
    PROCESSED(1, "已处理");

    private final Integer code;
    private final String desc;

    ActApprovalTaskStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static ActApprovalTaskStatusEnum getByCode(Integer code) {
        return Arrays.stream(values())
            .filter(e -> e.code.equals(code))
            .findFirst()
            .orElse(null);
    }
}
