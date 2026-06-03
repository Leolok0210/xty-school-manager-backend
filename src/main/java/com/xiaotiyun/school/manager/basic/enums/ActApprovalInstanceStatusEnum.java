package com.xiaotiyun.school.manager.basic.enums;

import java.util.Arrays;

public enum ActApprovalInstanceStatusEnum {
    RUNNING(1, "运行中"),
    COMPLETED(2, "已完成"),
    REJECTED(3, "已拒绝"),
    REVOKE(4, "已撤销");

    private final Integer code;
    private final String desc;

    ActApprovalInstanceStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static ActApprovalInstanceStatusEnum getByCode(Integer code) {
        return Arrays.stream(values())
            .filter(e -> e.code.equals(code))
            .findFirst()
            .orElse(null);
    }
}
