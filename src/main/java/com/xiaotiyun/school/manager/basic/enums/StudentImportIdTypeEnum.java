package com.xiaotiyun.school.manager.basic.enums;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

public enum StudentImportIdTypeEnum {
    ID_CARD(1, "澳門居民身份證", "Macao Resident Identity Card", "Bilhete de Identidade de Residente"),
    PASSPORT(2, "護照", "Passport", "Passaporte"),
    RESIDENCE_PERMIT(4, "港澳台居民居住證", "Residence Permit for Hong Kong Macao and Taiwan Residents", "Cartão de Residência para Residentes de Hong Kong Macau e Taiwan"),
    FOREIGNER_PERMIT(5, "外國人永久居留身份證（綠卡）", "Foreign Permanent Resident Identity Card (Green Card)", "Cartão de Identificação de Residente Permanente para Estrangeiros (Cartão Verde)");

    @Getter
    private final int code;
    private final String zhTwValue;
    private final String enValue;
    private final String ptValue;

    StudentImportIdTypeEnum(int code, String zhTwValue, String enValue, String ptValue) {
        this.code = code;
        this.zhTwValue = zhTwValue;
        this.enValue = enValue;
        this.ptValue = ptValue;
    }

    public static String getValue(int code, SchoolLanguageEnum language) {
        for (StudentImportIdTypeEnum idType : values()) {
            if (idType.code == code) {
                switch (language) {
                    case EN_US:
                        return idType.enValue;
                    case PT_PT:
                        return idType.ptValue;
                    default:
                        return idType.zhTwValue;
                }
            }
        }
        return "";
    }

    public static Integer getCode(String value, SchoolLanguageEnum language) {
        if (StringUtils.isNotBlank(value)) {
            for (StudentImportIdTypeEnum studentImportIdTypeEnum : values()) {
                switch (language) {
                    case EN_US:
                        if (studentImportIdTypeEnum.enValue.equals(value)) return studentImportIdTypeEnum.code;
                        break;
                    case PT_PT:
                        if (studentImportIdTypeEnum.ptValue.equals(value)) return studentImportIdTypeEnum.code;
                        break;
                    default:
                        if (studentImportIdTypeEnum.zhTwValue.equals(value)) return studentImportIdTypeEnum.code;
                }
            }
        }
        return null;
    }

    public static StudentImportIdTypeEnum toEnum(String value) {
        for (StudentImportIdTypeEnum ele : values()) {
            if (ele.zhTwValue.equals(value) || ele.enValue.equals(value) || ele.ptValue.equals(value)) {
                return ele;
            }
        }
        return null;
    }
}
