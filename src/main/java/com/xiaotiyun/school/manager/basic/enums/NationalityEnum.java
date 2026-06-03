package com.xiaotiyun.school.manager.basic.enums;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

public enum NationalityEnum {
    PORTUGAL("PT", "葡國", "PORTUGAL", "Portuguesa"),
    CHINA("CN", "中國", "CHINA", "Chinesa"),
    UNITED_KINGDOM("GB", "英國", "UNITED KINGDOM", "Inglesa"),
    UNITED_STATES("US", "美國", "UNITED STATES", "Estados Unidos"),
    PHILIPPINES("PH", "菲律賓", "PHILIPPINES", "Filipina"),
    CANADA("CA", "加拿大", "CANADA", "Canadá"),
    MYANMAR("MM", "緬甸", "MYANMAR", "Birmânia"),
    THAILAND("TH", "泰國", "THAILAND", "Tailândia"),
    COSTA_RICA("CR", "哥斯達黎加", "COSTA RICA", "Costa Rica"),
    TONGA("TO", "東加王國", "TONGA", "Tonga"),
    VENEZUELA("VE", "委內瑞拉", "VENEZUELA", "Venezuela"),
    AUSTRALIA("AU", "澳洲", "AUSTRALIA", "Austrália"),
    JAPAN("JP", "日本", "JAPAN", "Japão"),
    SOUTH_KOREA("KR", "韓國", "SOUTH KOREA", "Coreia do Sul"),
    PERU("PE", "秘魯", "PERU", "Peru"),
    INDIA("IN", "印度", "INDIA", "Índia"),
    HONDURAS("HN", "洪都拉斯", "HONDURAS", "Honduras"),
    DOMINICAN_REPUBLIC("DO", "多明尼加共", "DOMINICAN REPUBLIC", "República Dominicana"),
    DOMINICA("DM", "多米尼克", "DOMINICA", "Dominica"),
    MALAYSIA("MY", "馬來西亞", "MALAYSIA", "Malásia"),
    BRAZIL("BR", "巴西", "BRAZIL", "Brasil"),
    INDONESIA("ID", "印尼", "INDONESIA", "Indonésia"),
    MADAGASCAR("MG", "馬達加斯加", "MADAGASCAR", "Madagáscar"),
    SIERRA_LEONE("SL", "塞拉里昂", "SIERRA LEONE", "Serra Leoa"),
    NEW_ZEALAND("NZ", "紐西蘭", "NEW ZEALAND", "Nova Zelândia"),
    FRANCE("FR", "法國", "FRANCE", "França"),
    SRI_LANKA("LK", "斯里蘭卡", "SRI LANKA", "Sri Lanka"),
    IRELAND("IE", "愛爾蘭", "IRELAND", "Irlanda"),
    ECUADOR("EC", "厄瓜多爾", "ECUADOR", "Equador"),
    BELIZE("BZ", "伯利茲", "BELIZE", "Belize"),
    GERMANY("DE", "德國", "GERMANY", "Alemanha"),
    SINGAPORE("SG", "新加坡", "SINGAPORE", "Singapura"),
    PANAMA("PA", "巴拿馬", "PANAMA", "Panamá"),
    MAURITIUS("MU", "毛里求斯", "MAURITIUS", "Maurícias"),
    NICARAGUA("NI", "尼加拉瓜", "NICARAGUA", "Nicarágua"),
    GUATEMALA("GT", "危地馬拉", "GUATEMALA", "Guatemala"),
    SPAIN("ES", "西班牙", "SPAIN", "Espanha"),
    TANZANIA("TZ", "坦桑尼亞", "TANZANIA", "Tanzânia"),
    MOZAMBIQUE("MZ", "莫桑比克", "MOZAMBIQUE", "Moçambique"),
    FIJI("FJ", "斐濟", "FIJI", "Fiji"),
    NORTH_KOREA("KP", "朝鮮", "NORTH KOREA", "Coreia do Norte"),
    AUSTRIA("AT", "奧地利", "AUSTRIA", "Áustria"),
    ARGENTINA("AR", "阿根廷", "ARGENTINA", "Argentina"),
    SOUTH_AFRICA("ZA", "南非", "SOUTH AFRICA", "África do Sul"),
    BOLIVIA("BO", "玻利維亞", "BOLIVIA", "Bolívia"),
    EGYPT("EG", "埃及", "EGYPT", "Egito"),
    NORWAY("NO", "挪威", "NORWAY", "Noruega"),
    SWITZERLAND("CH", "瑞士", "SWITZERLAND", "Suíça"),
    SWEDEN("SE", "瑞典", "SWEDEN", "Suécia"),
    PAPUA_NEW_GUINEA("PG", "巴布亞新畿", "PAPUA NEW GUINEA", "Papua Nova Guiné"),
    UGANDA("UG", "烏干達", "UGANDA", "Uganda"),
    MEXICO("MX", "墨西哥", "MEXICO", "México"),
    ITALY("IT", "意大利", "ITALY", "Itália"),
    PAKISTAN("PK", "巴基斯坦", "PAKISTAN", "Paquistão"),
    SEYCHELLES("SC", "塞席爾", "SEYCHELLES", "Seicheles"),
    CUBA("CU", "古巴", "CUBA", "Cuba"),
    MALTA("MT", "馬耳他", "MALTA", "Malta"),
    BELGIUM("BE", "比利時", "BELGIUM", "Bélgica"),
    VIETNAM("VN", "越南", "VIETNAM", "Vietname"),
    NETHERLANDS("NL", "荷蘭", "NETHERLANDS", "Holanda"),
    SAINT_LUCIA("LC", "聖露西亞", "SAINT LUCIA", "Santa Lúcia"),
    DENMARK("DK", "丹麥", "DENMARK", "Dinamarca"),
    CAMBODIA("KH", "柬埔寨", "CAMBODIA", "Camboja"),
    TRINIDAD("TT", "千里達", "TRINIDAD", "Trindade"),
    URUGUAY("UY", "烏拉圭", "URUGUAY", "Uruguai"),
    POLAND("PL", "波蘭", "POLAND", "Polónia"),
    GUINEA_BISSAU("GW", "幾內亞比紹", "GUINEA-BISSAU", "Guiné-Bissau"),
    ANGOLA("AO", "安哥拉", "ANGOLA", "Angola"),
    RUSSIA("RU", "俄羅斯", "RUSSIA", "Rússia"),
    YUGOSLAVIA("YU", "南斯拉夫", "YUGOSLAVIA", "Jugoslávia"),
    COLOMBIA("CO", "哥倫比亞", "COLOMBIA", "Colômbia"),
    CHILE("CL", "智利", "CHILE", "Chile"),
    HUNGARY("HU", "匈牙利", "HUNGARY", "Hungria"),
    SAMOA("WS", "西薩摩亞", "SAMOA", "Samoa Ocidental"),
    GAMBIA("GM", "岡比亞", "GAMBIA", "Gâmbia"),
    NAURU("NR", "諾魯/瑙魯", "NAURU", "Nauru"),
    SAO_TOME_AND_PRINCIPE("ST", "聖多美和林西比", "SÃO TOMÉ AND PRÍNCIPE", "São Tomé e Príncipe"),
    KIRIBATI("KI", "基里巴斯", "KIRIBATI", "Kiribati"),
    NEPAL("NP", "尼泊爾", "NEPAL", "Nepal"),
    JAMAICA("JM", "牙買加", "JAMAICA", "Jamaica"),
    FINLAND("FI", "芬蘭", "FINLAND", "Finlândia"),
    BULGARIA("BG", "保加利亞", "BULGARIA", "Bulgária"),
    TURKEY("TR", "土耳其", "TURKEY", "Turquia"),
    NIGERIA("NG", "尼日利亞", "NIGERIA", "Nigéria"),
    UNITED_ARAB_EMIRATES("AE", "阿拉伯聯合酋長國", "UNITED ARAB EMIRATES", "Emirados Árabes Unidos"),
    TOGO("TG", "多哥", "TOGO", "Togo"),
    ROMANIA("RO", "羅馬尼亞", "ROMANIA", "Roménia"),
    ARMENIA("AM", "亞美尼亞", "ARMENIA", "Arménia"),
    DUBAI("AE", "杜拜", "DUBAI", "Dubai"),
    MALDIVES("MV", "馬爾代夫", "MALDIVES", "Maldivas"),
    GREECE("GR", "希臘", "GREECE", "Grécia"),
    UKRAINE("UA", "烏克蘭", "UKRAINE", "Ucrânia"),
    SYRIA("SY", "敘利亞", "SYRIA", "Síria"),
    MARSHALL_ISLANDS("MH", "馬紹爾群島", "MARSHALL ISLANDS", "Ilhas Marshall"),
    CZECK_REPUBLIC("CZ", "捷克", "CZECK REPUBLIC", "República Checa"),
    UNKNOWN("XX", "不詳", "UNKNOWN", "Desconhecido");

    @Getter
    private final String code;
    @Getter
    private final String zhTwValue;
    @Getter
    private final String enValue;
    @Getter
    private final String ptValue;

    NationalityEnum(String code, String zhTwValue, String enValue, String ptValue) {
        this.code = code;
        this.zhTwValue = zhTwValue;
        this.enValue = enValue;
        this.ptValue = ptValue;
    }

    public static String getValue(String code, SchoolLanguageEnum language) {
        if (StringUtils.isNotBlank(code)) {
            for (NationalityEnum nationality : values()) {
                if (nationality.code.equals(code)) {
                    switch (language) {
                        case EN_US:
                            return nationality.enValue;
                        case PT_PT:
                            return nationality.ptValue;
                        default:
                            return nationality.zhTwValue;
                    }
                }
            }
        }
        return "";
    }

    public static String getCode(String value, SchoolLanguageEnum language) {
        if (StringUtils.isNotBlank(value)) {
            for (NationalityEnum nationality : values()) {
                switch (language) {
                    case EN_US:
                        if (nationality.enValue.equals(value)) return nationality.code;
                        break;
                    case PT_PT:
                        if (nationality.ptValue.equals(value)) return nationality.code;
                        break;
                    default:
                        if (nationality.zhTwValue.equals(value)) return nationality.code;
                }
            }
        }
        return null;
    }
}