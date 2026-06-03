package com.xiaotiyun.school.manager.basic.enums;

public enum CompetitionTypeEnum {
    INDIVIDUAL(1, "个人赛"),
    TEAM(2, "团体赛");

    private int code;
    private String value;

    CompetitionTypeEnum(int code, String value) {
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
        for (CompetitionTypeEnum ele : values()) {
            if (ele.getCode() == code) return ele.getValue();
        }
        return null;
    }
}