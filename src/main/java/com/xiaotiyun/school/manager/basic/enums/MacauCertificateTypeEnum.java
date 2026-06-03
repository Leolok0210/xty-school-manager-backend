package com.xiaotiyun.school.manager.basic.enums;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

public enum MacauCertificateTypeEnum {
    MACAU_RESIDENT_ID_CARD(1, "澳門居民身份證", "Macau Resident ID Card", "Bilhete de Identidade de Residente de Macau"),
    HONG_KONG_RESIDENT_ID_CARD(2, "香港居民身份證", "Hong Kong Resident ID Card", "Bilhete de Identidade de Residente de Hong Kong"),
    EXIT_ENTRY_PERMIT(3, "往來港澳通行證（通行證）", "Exit-Entry Permit", "Autorização de Entrada/Saída"),
    OTHER(4, "其他", "Other", "Outro");

    @Getter
    private final int code;
    private final String zhTwValue;
    private final String enValue;
    private final String ptValue;

    MacauCertificateTypeEnum(int code, String zhTwValue, String enValue, String ptValue) {
        this.code = code;
        this.zhTwValue = zhTwValue;
        this.enValue = enValue;
        this.ptValue = ptValue;
    }

    public static String getValue(int code, SchoolLanguageEnum language) {
        for (MacauCertificateTypeEnum mainlandCertificateTypeEnum : values()) {
            if (mainlandCertificateTypeEnum.code == code) {
                switch (language) {
                    case EN_US:
                        return mainlandCertificateTypeEnum.enValue;
                    case PT_PT:
                        return mainlandCertificateTypeEnum.ptValue;
                    default:
                        return mainlandCertificateTypeEnum.zhTwValue;
                }
            }
        }
        return "";
    }

    public static Integer getCode(String value, SchoolLanguageEnum language) {
        if (StringUtils.isNotBlank(value)) {
            for (MacauCertificateTypeEnum mainlandCertificateTypeEnum : values()) {
                switch (language) {
                    case EN_US:
                        if (mainlandCertificateTypeEnum.enValue.equals(value)) return mainlandCertificateTypeEnum.code;
                        break;
                    case PT_PT:
                        if (mainlandCertificateTypeEnum.ptValue.equals(value)) return mainlandCertificateTypeEnum.code;
                        break;
                    default:
                        if (mainlandCertificateTypeEnum.zhTwValue.equals(value)) return mainlandCertificateTypeEnum.code;
                }
            }
        }
        return null;
    }
}
