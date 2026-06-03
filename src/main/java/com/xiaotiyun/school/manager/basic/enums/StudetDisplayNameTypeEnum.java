package com.xiaotiyun.school.manager.basic.enums;

public enum StudetDisplayNameTypeEnum {
    CHINESE(1, "中文姓名"),
    ENGLISH(2, "外文姓名");

    private int code;
    private String value;

    StudetDisplayNameTypeEnum(int code, String value) {
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
        for (StudetDisplayNameTypeEnum ele : values()) {
            if (ele.getCode() == code) return ele.getValue();
        }
        return null;
    }
}
