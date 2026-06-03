package com.xiaotiyun.school.manager.basic.enums;

public enum ImportTaskStatusEnum {
    UNTREATED(0, "待处理"),
    IN_PROCESS(1, "处理中"),
    HANDLED(2, "已处理");

    private int code;
    private String value;

    ImportTaskStatusEnum(int code, String value) {
        this.code = code;
        this.value = value;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public static String getValue(int code) {
        for (ImportTaskStatusEnum ele : values()) {
            if (ele.getCode() == code) return ele.getValue();
        }
        return null;
    }
}
