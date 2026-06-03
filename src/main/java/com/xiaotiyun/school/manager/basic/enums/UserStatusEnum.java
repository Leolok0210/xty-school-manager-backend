package com.xiaotiyun.school.manager.basic.enums;

import lombok.Getter;

public enum UserStatusEnum {
    ACTIVE(1, "在職", "Active", "Ativo"),
    INACTIVE(2, "離職", "Inactive", "Inativo");

    @Getter
    private final int code;
    private final String zhTwValue;
    private final String enValue;
    private final String ptValue;

    UserStatusEnum(int code, String zhTwValue, String enValue, String ptValue) {
        this.code = code;
        this.zhTwValue = zhTwValue;
        this.enValue = enValue;
        this.ptValue = ptValue;
    }

    public static String getValue(int code, SchoolLanguageEnum language) {
        for (UserStatusEnum status : values()) {
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
