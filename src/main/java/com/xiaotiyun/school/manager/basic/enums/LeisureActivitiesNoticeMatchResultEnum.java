package com.xiaotiyun.school.manager.basic.enums;

public enum LeisureActivitiesNoticeMatchResultEnum {
    MATCH_SUCCESS(1, "匹配成功"),
    MATCH_FAIL(2, "匹配失败");

    private int code;
    private String value;

    LeisureActivitiesNoticeMatchResultEnum(int code, String value) {
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
        for (LeisureActivitiesNoticeMatchResultEnum ele : values()) {
            if (ele.getCode() == code) return ele.getValue();
        }
        return null;
    }
}