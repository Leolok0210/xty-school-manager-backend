package com.xiaotiyun.school.manager.basic.enums;

public enum LeisureActivitiesNoticeConsultEnum {
    NOT_VIEWED(0, "未查阅"),
    VIEWED(1, "已查阅");

    private int code;
    private String value;

    LeisureActivitiesNoticeConsultEnum(int code, String value) {
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
        for (LeisureActivitiesNoticeConsultEnum ele : values()) {
            if (ele.getCode() == code) return ele.getValue();
        }
        return null;
    }
}