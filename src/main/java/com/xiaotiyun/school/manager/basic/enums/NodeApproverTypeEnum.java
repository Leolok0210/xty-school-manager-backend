package com.xiaotiyun.school.manager.basic.enums;

import lombok.Getter;

public enum NodeApproverTypeEnum {
    MANUAL(1, "人工审批"),
    AUTO(2, "自动审批");

    @Getter
    private final Integer code;
    @Getter
    private final String desc;

    NodeApproverTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

}
