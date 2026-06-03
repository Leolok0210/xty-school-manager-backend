package com.xiaotiyun.school.manager.basic.enums;

import lombok.Getter;

public enum StudentStatusEnum {
    AT_SCHOOL(1, "在校", "At School", "Na Escola"),
    GRADUATE(2, "毕业", "Graduate", "Graduado"),
    DROP_OUT(3, "退学", "Drop Out", "Desistir"),
    SUSPEND_SCHOOLING(4, "休学", "Suspend Schooling", "Suspender Estudos"),
    TRANSFER_SCHOOL(5, "转学", "Transfer School", "Transferir Escola");

    @Getter
    private final int code;
    private final String zhTwValue;
    private final String enValue;
    private final String ptValue;

    StudentStatusEnum(int code, String zhTwValue, String enValue, String ptValue) {
        this.code = code;
        this.zhTwValue = zhTwValue;
        this.enValue = enValue;
        this.ptValue = ptValue;
    }

    public static String getValue(int code, SchoolLanguageEnum language) {
        for (StudentStatusEnum status : values()) {
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
