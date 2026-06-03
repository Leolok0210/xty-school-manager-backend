package com.xiaotiyun.school.manager.basic.enums;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.formula.functions.T;

public enum HkMacauPassVisaTypeEnum {
    VISIT_ENDORSEMENT(1, "探視簽注（T）", "Visit Endorsement (T)", "Visto de Visita (T)"),
    INDIVIDUAL_TRAVEL_ENDORSEMENT(2, "個人旅遊簽注（G）", "Individual Travel Endorsement (G)", "Visto de Viagem Individual (G)"),
    BUSINESS_ENDORSEMENT(3, "商務簽注（S）", "Business Endorsement (S)", "Visto de Negócios (S)"),
    STAY_ENDORSEMENT(4, "逗留簽注（D）", "Stay Endorsement (D)", "Visto de Permanência (D)"),
    GROUP_TRAVEL_ENDORSEMENT(5, "團體旅遊簽注（L）", "Group Travel Endorsement (L)", "Visto de Viagem em Grupo (L)"),
    OTHER_ENDORSEMENT(6, "其他簽注（Q）", "Other Endorsement (Q)", "Outro Visto (Q)");

    @Getter
    private final int code;
    private final String zhTwValue;
    private final String enValue;
    private final String ptValue;

    HkMacauPassVisaTypeEnum(int code, String zhTwValue, String enValue, String ptValue) {
        this.code = code;
        this.zhTwValue = zhTwValue;
        this.enValue = enValue;
        this.ptValue = ptValue;
    }

    public static String getValue(int code, SchoolLanguageEnum language) {
        for (HkMacauPassVisaTypeEnum mainlandCertificateTypeEnum : values()) {
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
            for (HkMacauPassVisaTypeEnum mainlandCertificateTypeEnum : values()) {
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
