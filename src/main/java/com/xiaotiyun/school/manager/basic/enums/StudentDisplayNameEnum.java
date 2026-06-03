package com.xiaotiyun.school.manager.basic.enums;

import lombok.Getter;

public enum StudentDisplayNameEnum {
    CHINESE_NAME(1, "中文姓名", "Chinese Name", "Nome em Chinês"),
    ENGLISH_NAME(2, "外文姓名", "English Name", "Nome em Inglês");

    @Getter
    private final int code;
    private final String zhTwValue;
    private final String enValue;
    private final String ptValue;

    StudentDisplayNameEnum(int code, String zhTwValue, String enValue, String ptValue) {
        this.code = code;
        this.zhTwValue = zhTwValue;
        this.enValue = enValue;
        this.ptValue = ptValue;
    }

    public static String getValue(int code, SchoolLanguageEnum language) {
        for (StudentDisplayNameEnum studentDisplayNameEnum : values()) {
            if (studentDisplayNameEnum.code == code) {
                switch (language) {
                    case EN_US:
                        return studentDisplayNameEnum.enValue;
                    case PT_PT:
                        return studentDisplayNameEnum.ptValue;
                    default:
                        return studentDisplayNameEnum.zhTwValue;
                }
            }
        }
        return "";
    }
}
