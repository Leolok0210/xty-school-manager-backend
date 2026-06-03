package com.xiaotiyun.school.manager.basic.enums;

import java.util.Arrays;

/**
 * 审批结果枚举
 */
public enum ApprovalResultEnum {
    APPROVED(1, "通过"),
    REJECTED(2, "拒绝");

    private final Integer code;
    private final String desc;

    ApprovalResultEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static ApprovalResultEnum getByCode(Integer code) {
        return Arrays.stream(values())
            .filter(e -> e.code.equals(code))
            .findFirst()
            .orElse(null);
    }
}
