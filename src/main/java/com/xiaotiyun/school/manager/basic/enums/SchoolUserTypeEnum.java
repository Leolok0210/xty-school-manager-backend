package com.xiaotiyun.school.manager.basic.enums;

import lombok.Getter;

public enum SchoolUserTypeEnum {
    TEACHER(1, "教師", "Teacher", "Professor"),
    STAFF(2, "職員", "Staff", "Funcionário"),
    PROFESSIONAL(3, "專職人員", "Professional", "Profissional"),
    WORKER(4, "工友", "Worker", "Trabalhador");

    @Getter
    private final int code;
    private final String zhTwValue;
    private final String enValue;
    private final String ptValue;

    SchoolUserTypeEnum(int code, String zhTwValue, String enValue, String ptValue) {
        this.code = code;
        this.zhTwValue = zhTwValue;
        this.enValue = enValue;
        this.ptValue = ptValue;
    }

    public static String getValue(int code, SchoolLanguageEnum language) {
        for (SchoolUserTypeEnum status : values()) {
            if (status.code == code) {
                switch (language) {
                    case EN_US:
                        return status.enValue;
                    case PT_PT:
                        return status.ptValue;
                    default:
                        return status.zhTwValue;
                }
            }
        }
        return "";
    }
}
