package com.xiaotiyun.school.manager.basic.enums;

public enum DefaultParamEnum {
    REST("REST", "大小息"),
    PERF("PERF", "课堂"),
    APPEARANCE("APPEARANCE", "仪表不符"),
    ROUNDS("ROUNDS", "巡堂登记");

    private String code;
    private String value;

    DefaultParamEnum(String code, String value) {
        this.code = code;
        this.value = value;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public static DefaultParamEnum getByCode(String code) {
        for (DefaultParamEnum ele : values()) {
            if (ele.getCode().equals(code)) return ele;
        }
        return null;
    }
}
