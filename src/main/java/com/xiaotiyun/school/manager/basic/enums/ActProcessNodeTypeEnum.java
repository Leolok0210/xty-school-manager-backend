package com.xiaotiyun.school.manager.basic.enums;

import java.util.Arrays;

public enum ActProcessNodeTypeEnum {
    START(1, "开始节点"),
    APPROVER(2, "审批节点"),
    COPY(3, "抄送节点"),
    CONDITION(4, "条件分支节点"),
    GATEWAY(5, "网关节点");

    private final Integer code;
    private final String desc;

    ActProcessNodeTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static ActProcessNodeTypeEnum getByCode(Integer code) {
        return Arrays.stream(values())
            .filter(e -> e.code.equals(code))
            .findFirst()
            .orElse(null);
    }
}
