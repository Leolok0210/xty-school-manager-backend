package com.xiaotiyun.school.manager.basic.enums;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public enum GenderEnum {
    BOY(1, "男", "Male", "Masculino"),
    GIRL(2, "女", "Female", "Feminino");

    private int code;
    @Getter
    private final String zhTwValue;
    @Getter
    private final String enValue;
    @Getter
    private final String ptValue;

    GenderEnum(int code, String zhTwValue, String enValue, String ptValue) {
        this.code = code;
        this.zhTwValue = zhTwValue;
        this.enValue = enValue;
        this.ptValue = ptValue;
    }

    public static String getValue(int code, SchoolLanguageEnum language) {
        for (GenderEnum gender : values()) {
            if (gender.code == code) {
                switch (language) {
                    case EN_US:
                        return gender.enValue;
                    case PT_PT:
                        return gender.ptValue;
                    default:
                        return gender.zhTwValue;
                }
            }
        }
        return "";
    }

    public static List<String> allValues(SchoolLanguageEnum language) {
        List<String> list = new ArrayList<>();
        for (GenderEnum gender : values()) {
            switch (language) {
                case EN_US:
                    list.add(gender.enValue);
                    break;
                case PT_PT:
                    list.add(gender.ptValue);
                    break;
                default:
                    list.add(gender.zhTwValue);
                    break;
            }
        }
        return list;
    }

    public static Integer getCode(String value, SchoolLanguageEnum language) {
        for (GenderEnum ele : values()) {
            switch (language) {
                case EN_US:
                    if (ele.enValue.equals(value)) return ele.code;
                    break;
                case PT_PT:
                    if (ele.ptValue.equals(value)) return ele.code;
                    break;
                default:
                    if (ele.zhTwValue.equals(value)) return ele.code;
            }
        }
        return null;
    }
}
