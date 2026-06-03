package com.xiaotiyun.school.manager.basic.enums;

import java.util.Arrays;

public enum ActApprovalRuleSettingEnum {
    FIRST_NODE_ONLY(1, "仅首个节点需审批"),
    ALL_NODES(2, "所有节点需审批"),
    AUTO_APPROVE_CONTINUOUS(3, "连续审批自动同意");

    private final Integer code;
    private final String desc;

    ActApprovalRuleSettingEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static ActApprovalRuleSettingEnum getByCode(Integer code) {
        return Arrays.stream(values())
            .filter(e -> e.code.equals(code))
            .findFirst()
            .orElse(null);
    }
}
