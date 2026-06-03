package com.xiaotiyun.school.manager.basic.enums;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

public enum AddressAreaEnum {
    MACAU_FATIMA(1, "[\"820000\",\"820100\",\"820101\"]", "澳门/澳門半島/花地瑪堂區", "Macau/Macau Peninsula/Fátima Parish", "Macau/Península de Macau/Freguesia de Nossa Senhora de Fátima"),
    MACAU_SANTO_ANTONIO(2, "[\"820000\",\"820100\",\"820109\"]", "澳门/澳門半島/聖安多尼堂區", "Macau/Macau Peninsula/Santo António Parish", "Macau/Península de Macau/Freguesia de Santo António"),
    MACAU_SAO_LAZARO(3, "[\"820000\",\"820100\",\"820103\"]", "澳门/澳門半島/望德堂區", "Macau/Macau Peninsula/São Lázaro Parish", "Macau/Península de Macau/Freguesia de São Lázaro"),
    MACAU_SE(4, "[\"820000\",\"820100\",\"820105\"]", "澳门/澳門半島/風順堂區", "Macau/Macau Peninsula/São Lourenço Parish", "Macau/Península de Macau/Freguesia de São Lourenço"),
    MACAU_SANTA_TERESA(5, "[\"820000\",\"820100\",\"820104\"]", "澳门/澳門半島/大堂區", "Macau/Macau Peninsula/Sé Parish", "Macau/Península de Macau/Freguesia da Sé"),
    MACAU_OUR_LADY_OF_CARMEL(6, "[\"820000\",\"820200\",\"820106\"]", "澳门/氹仔/嘉模堂區", "Macau/Taipa/Nossa Senhora do Carmo Parish", "Macau/Taipa/Freguesia de Nossa Senhora do Carmo"),
    MACAU_SAO_FRANCISCO_XAVIER(7, "[\"820000\",\"820300\",\"820108\"]", "澳门/路環/聖方濟各堂區", "Macau/Coloane/São Francisco Xavier Parish", "Macau/Coloane/Freguesia de São Francisco Xavier"),
    ZHUHAI(8, "[\"442000\"]", "珠海", "Zhuhai", "Zhuhai"),
    ZHONGSHAN(9, "[\"440400\"]", "中山", "Zhongshan", "Zhongshan"),
    OTHER(10, "[\"990000\"]", "其他", "Other", "Outro");

    @Getter
    private final int code;
    @Getter
    private final String areaCode;
    @Getter
    private final String zhTwValue;
    @Getter
    private final String enValue;
    @Getter
    private final String ptValue;

    AddressAreaEnum(int code, String areaCode, String zhTwValue, String enValue, String ptValue) {
        this.code = code;
        this.areaCode = areaCode;
        this.zhTwValue = zhTwValue;
        this.enValue = enValue;
        this.ptValue = ptValue;
    }

    public static String getValue(int code, SchoolLanguageEnum language) {
        for (AddressAreaEnum area : values()) {
            if (area.code == code) {
                switch (language) {
                    case EN_US:
                        return area.enValue;
                    case PT_PT:
                        return area.ptValue;
                    default:
                        return area.zhTwValue;
                }
            }
        }
        return "";
    }

    public static String getValue(String areaCode, SchoolLanguageEnum language) {
        for (AddressAreaEnum area : values()) {
            if (area.areaCode.equals(areaCode)) {
                switch (language) {
                    case EN_US:
                        return area.enValue;
                    case PT_PT:
                        return area.ptValue;
                    default:
                        return area.zhTwValue;
                }
            }
        }
        return "";
    }

    public static Integer getCode(String value, SchoolLanguageEnum language) {
        if (StringUtils.isNotBlank(value)) {
            for (AddressAreaEnum area : values()) {
                switch (language) {
                    case EN_US:
                        if (area.enValue.equals(value)) return area.code;
                        break;
                    case PT_PT:
                        if (area.ptValue.equals(value)) return area.code;
                        break;
                    default:
                        if (area.zhTwValue.equals(value)) return area.code;
                        break;
                }
            }
        }
        return null;
    }

    public static String getAreaCode(String value, SchoolLanguageEnum language) {
        if (StringUtils.isNotBlank(value)) {
            for (AddressAreaEnum area : values()) {
                switch (language) {
                    case EN_US:
                        if (area.enValue.equals(value)) return area.areaCode;
                        break;
                    case PT_PT:
                        if (area.ptValue.equals(value)) return area.areaCode;
                        break;
                    default:
                        if (area.zhTwValue.equals(value)) return area.areaCode;
                        break;
                }
            }
        }
        return "";
    }
}