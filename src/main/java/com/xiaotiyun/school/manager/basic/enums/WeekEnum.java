package com.xiaotiyun.school.manager.basic.enums;

public enum WeekEnum {
    MONDAY(1, "週一", "Monday", "Segunda-feira"),
    TUESDAY(2, "週二", "Tuesday", "Terça-feira"),
    WEDNESDAY(3, "週三", "Wednesday", "Quarta-feira"),
    THURSDAY(4, "週四", "Thursday", "Quinta-feira"),
    FRIDAY(5, "週五", "Friday", "Sexta-feira"),
    SATURDAY(6, "週六", "Saturday", "Sábado"),
    SUNDAY(7, "週日", "Sunday", "Domingo");

    private final int code;
    private final String zhTwValue;
    private final String enValue;
    private final String ptValue;

    WeekEnum(int code, String zhTwValue, String enValue, String ptValue) {
        this.code = code;
        this.zhTwValue = zhTwValue;
        this.enValue = enValue;
        this.ptValue = ptValue;
    }

    public static String getValue(int code, SchoolLanguageEnum language) {
        for (WeekEnum day : values()) {
            if (day.code == code) {
                switch (language) {
                    case EN_US:
                        return day.enValue;
                    case PT_PT:
                        return day.ptValue;
                    default:
                        return day.zhTwValue;
                }
            }
        }
        return "";
    }
}
