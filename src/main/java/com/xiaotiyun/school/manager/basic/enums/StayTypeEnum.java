package com.xiaotiyun.school.manager.basic.enums;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

public enum StayTypeEnum {
    PERMANENT(1, "永久", "Permanent", "Permanente"),
    TEMPORARY(2, "有限期", "Limited Validity", "Temporário"),
    OTHER(3, "其他", "Other", "Outro");

    @Getter
    private final int code;
    private final String zhTwValue;
    private final String enValue;
    private final String ptValue;

    StayTypeEnum(int code, String zhTwValue, String enValue, String ptValue) {
        this.code = code;
        this.zhTwValue = zhTwValue;
        this.enValue = enValue;
        this.ptValue = ptValue;
    }

    /**
     * 根据语言类型获取对应的枚举值
     *
     * @param code     枚举的code值
     * @param language 语言类型
     * @return 对应语言的枚举值
     */
    public static String getValue(int code, SchoolLanguageEnum language) {
        for (StayTypeEnum stayTypeEnum : values()) {
            if (stayTypeEnum.code == code) {
                switch (language) {
                    case EN_US:
                        return stayTypeEnum.enValue;
                    case PT_PT:
                        return stayTypeEnum.ptValue;
                    default:
                        return stayTypeEnum.zhTwValue;
                }
            }
        }
        return "";
    }

    public static Integer getCode(String value, SchoolLanguageEnum language) {
        if (StringUtils.isNotBlank(value)) {
            for (StayTypeEnum stayTypeEnum : values()) {
                switch (language) {
                    case EN_US:
                        if (stayTypeEnum.enValue.equals(value)) return stayTypeEnum.code;
                        break;
                    case PT_PT:
                        if (stayTypeEnum.ptValue.equals(value)) return stayTypeEnum.code;
                        break;
                    default:
                        if (stayTypeEnum.zhTwValue.equals(value)) return stayTypeEnum.code;
                }
            }
        }
        return null;
    }
}
