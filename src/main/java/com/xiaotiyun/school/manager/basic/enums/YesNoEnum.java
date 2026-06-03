package com.xiaotiyun.school.manager.basic.enums;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

public enum YesNoEnum {
    YES(1, "是", "Yes", "Sim"),
    NO(0, "否", "No", "Não");

    @Getter
    private final int code;
    @Getter
    private final String zhTwValue;
    @Getter
    private final String enValue;
    @Getter
    private final String ptValue;

    YesNoEnum(int code, String zhTwValue, String enValue, String ptValue) {
        this.code = code;
        this.zhTwValue = zhTwValue;
        this.enValue = enValue;
        this.ptValue = ptValue;
    }

    public static String getValue(int code, SchoolLanguageEnum language) {
        for (YesNoEnum item : values()) {
            if (item.code == code) {
                switch (language) {
                    case EN_US:
                        return item.enValue;
                    case PT_PT:
                        return item.ptValue;
                    default:
                        return item.zhTwValue;
                }
            }
        }
        return "";
    }

    public static Integer getCode(String value, SchoolLanguageEnum language) {
        if (StringUtils.isNotBlank(value)) {
            for (YesNoEnum item : values()) {
                switch (language) {
                    case EN_US:
                        if (item.enValue.equals(value)) return item.code;
                        break;
                    case PT_PT:
                        if (item.ptValue.equals(value)) return item.code;
                        break;
                    default:
                        if (item.zhTwValue.equals(value)) return item.code;
                }
            }
        }
        return null;
    }
}