package com.xiaotiyun.school.manager.basic.enums;

import java.util.Arrays;

public enum ComparisonOperatorEnum {
    LESS_THAN("lt", "小于"),
    LESS_EQUAL("le", "小于等于"),
    EQUAL("eq", "等于"),
    GREATER_THAN("gt", "大于"),
    GREATER_EQUAL("ge", "大于等于"),
    BETWEEN("between", "介于");

    private final String code;
    private final String desc;

    ComparisonOperatorEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static ComparisonOperatorEnum getByCode(String code) {
        return Arrays.stream(values())
            .filter(e -> e.code.equals(code))
            .findFirst()
            .orElse(null);
    }
}
