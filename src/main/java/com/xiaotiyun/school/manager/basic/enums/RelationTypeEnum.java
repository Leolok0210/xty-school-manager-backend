package com.xiaotiyun.school.manager.basic.enums;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

public enum RelationTypeEnum {
    FATHER(1, "父親", "Father", "Pai"),
    MOTHER(2, "母親", "Mother", "Mãe"),
    GRANDFATHER(3, "爺爺", "Paternal Grandfather", "Avô paterno"),
    GRANDMOTHER(4, "奶奶", "Paternal Grandmother", "Avó paterna"),
    OTHER(5, "其他", "Other", "Outro"),
    FATHER_SISTER(6, "外公", "Maternal Grandfather", "Avô materno"),
    MOTHER_SISTER(7, "外婆", "Maternal Grandmother", "Avó materna"),
    ;

    @Getter
    private final int code;
    private final String zhTwValue;
    private final String enValue;
    private final String ptValue;

    RelationTypeEnum(int code, String zhTwValue, String enValue, String ptValue) {
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
        for (RelationTypeEnum stayTypeEnum : values()) {
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
            for (RelationTypeEnum stayTypeEnum : values()) {
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
