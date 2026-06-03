package com.xiaotiyun.school.manager.basic.enums;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

public enum MainlandCertificateTypeEnum {
    HOME_RETURN_PERMIT(1, "港澳居民來往內地通行證（回鄉證）", "Home Return Permit", "Autorização de Regresso"),
    EXIT_ENTRY_PERMIT(2, "往來港澳通行證（通行證）", "Exit-Entry Permit", "Autorização de Entrada/Saída"),
    OTHER(3, "其他", "Other", "Outro");

    @Getter
    private final int code;
    private final String zhTwValue;
    private final String enValue;
    private final String ptValue;

    MainlandCertificateTypeEnum(int code, String zhTwValue, String enValue, String ptValue) {
        this.code = code;
        this.zhTwValue = zhTwValue;
        this.enValue = enValue;
        this.ptValue = ptValue;
    }

    public static String getValue(int code, SchoolLanguageEnum language) {
        for (MainlandCertificateTypeEnum mainlandCertificateTypeEnum : values()) {
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
            for (MainlandCertificateTypeEnum mainlandCertificateTypeEnum : values()) {
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
