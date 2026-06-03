package com.xiaotiyun.school.manager.basic.enums;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

public enum BirthPlaceEnum {
    MACAU("MO", "澳門", "MACAU", "Macau"),
    CHINA_CONTINENTAL("CN", "內地", "CHINA CONTINENTAL", "China Continental"),
    PORTUGAL("PT", "葡國", "PORTUGAL", "Portugal"),
    HONG_KONG("HK", "香港", "HONG KONG", "Hong Kong"),
    ESTADOS_UNIDOS("US", "美國", "ESTADOS UNIDOS", "Estados Unidos"),
    BIRMANIA("MM", "緬甸", "BIRMANIA", "Birmânia"),
    FILIPINA("PH", "菲律賓", "FILIPINA", "Filipinas"),
    CANADA("CA", "加拿大", "CANADA", "Canadá"),
    TAILANDIA("TH", "泰國", "TAILANDIA", "Tailândia"),
    BRASIL("BR", "巴西", "BRASIL", "Brasil"),
    COSTA_RICA("CR", "哥斯達黎加", "COSTA RICA", "Costa Rica"),
    AUSTRALIA("AU", "澳洲", "AUSTRALIA", "Austrália"),
    VENEZUELA("VE", "委內瑞拉", "VENEZUELA", "Venezuela"),
    MOCAMBIQUE("MZ", "莫桑比克", "MOCAMBIQUE", "Moçambique"),
    MADAGASCAR("MG", "馬達加斯加", "MADAGASCAR", "Madagáscar"),
    INGLATERRA("GB", "英國", "INGLATERRA", "Inglaterra"),
    PERU("PE", "秘魯", "PERU", "Peru"),
    COREIA_DO_SUL("KR", "韓國", "COREIA DO SUL", "Coreia do Sul"),
    INDONESIA("ID", "印尼", "INDONESIA", "Indonésia"),
    ANGOLA("AO", "安哥拉", "ANGOLA", "Angola"),
    MALASIA("MY", "馬來西亞", "MALASIA", "Malásia"),
    JAPAO("JP", "日本", "JAPAO", "Japão"),
    IRLANDA("IE", "愛爾蘭", "IRLANDA", "Irlanda"),
    INDIA("IN", "印度", "INDIA", "Índia"),
    FRANCA("FR", "法國", "FRANCA", "França"),
    SINGAPURA("SG", "新加坡", "SINGAPURA", "Singapura"),
    REPUBLICA_DOMINICANA("DO", "多明尼加", "REPUBLICA DOMINICANA", "República Dominicana"),
    DOMINICA("DM", "多米尼克", "DOMINICA", "Dominica"),
    COLOMBIA("CO", "哥倫比亞", "COLOMBIA", "Colômbia"),
    SRI_LANKA("LK", "斯里蘭卡", "SRI LANKA", "Sri Lanka"),
    PANAMA("PA", "巴拿馬", "PANAMA", "Panamá"),
    MAURICIAS("MU", "毛里求斯", "MAURICIAS", "Maurícias"),
    NICARAGUA("NI", "尼加拉瓜", "NICARAGUA", "Nicarágua"),
    AFRICA_DO_SUL("ZA", "南非", "AFRICA DO SUL", "África do Sul"),
    NOVA_ZELANDIA("NZ", "紐西蘭", "NOVA ZELANDIA", "Nova Zelândia"),
    TONGA("TO", "東加王國", "TONGA", "Tonga"),
    EQUADOR("EC", "厄瓜多爾", "EQUADOR", "Equador"),
    GUATEMALA("GT", "危地馬拉", "GUATEMALA", "Guatemala"),
    LAOS("LA", "老撾", "LAOS", "Laos"),
    HONDURAS("HN", "洪都拉斯", "HONDURAS", "Honduras"),
    FIJI("FJ", "斐濟", "FIJI", "Fiji"),
    ALEMANHA("DE", "德國", "ALEMANHA", "Alemanha"),
    TANZANIA("TZ", "坦桑尼亞", "TANZANIA", "Tanzânia"),
    CAMBOJA("KH", "柬埔寨", "CAMBOJA", "Camboja"),
    NO_MAR("ATSEA", "海上", "NO MAR", "No Mar"),
    LIBERIA("LR", "利比里亞", "LIBERIA", "Libéria"),
    TRINDADE("TT", "千里達", "TRINDADE", "Trindade"),
    ESPANHA("ES", "西班牙", "ESPANHA", "Espanha"),
    BELIZE("BZ", "伯利茲", "BELIZE", "Belize"),
    ARGENTINA("AR", "阿根廷", "ARGENTINA", "Argentina"),
    BOLIVIA("BO", "玻利維亞", "BOLIVIA", "Bolívia"),
    EGIPTO("EG", "埃及", "EGIPTO", "Egito"),
    HOLANDA("NL", "荷蘭", "HOLANDA", "Holanda"),
    COREIA("KP", "朝鮮", "COREIA", "Coreia"),
    VIETNAME("VN", "越南", "VIETNAME", "Vietname"),
    ITALIA("IT", "意大利", "ITALIA", "Itália"),
    SALVADOR("SV", "薩爾瓦多", "SALVADOR", "Salvador"),
    RUSSIA("RU", "俄羅斯", "RUSSIA", "Rússia"),
    PAPUA_NOVA_GUINE("PG", "新畿內亞", "PAPUA NOVA GUINE", "Papua Nova Guiné"),
    UGANDA("UG", "烏干達", "UGANDA", "Uganda"),
    MEXICO("MX", "墨西哥", "MEXICO", "México"),
    E_AFRICA("EAF", "東非", "E. AFRICA", "África Oriental"),
    SEYCHEUES("SC", "塞席爾", "SEYCHEUES", "Seicheles"),
    IRAN("IR", "伊朗", "IRAN", "Irã"),
    TAIWAN("TW", "臺灣", "TAIWAN", "Taiwan"),
    PAKISTAN("PK", "巴基斯坦", "PAKISTAN", "Paquistão"),
    BAHRAIN("BH", "巴林", "BAHRAIN", "Bahrein"),
    CUBA("CU", "古巴", "CUBA", "Cuba"),
    SWEDEN("SE", "瑞典", "SWEDEN", "Suécia"),
    MALTA("MT", "馬耳他", "MALTA", "Malta"),
    BELGICA("BE", "比利時", "BELGICA", "Bélgica"),
    AUSTRIA("AT", "奧地利", "AUSTRIA", "Áustria"),
    TIMOR("TL", "東帝汶", "Timor", "Timor-Leste"),
    GUINE_BASSEU("GW", "幾內亞比紹", "Guine Basseu", "Guiné-Bissau"),
    NORUEGA("NO", "挪威", "Noruega", "Noruega"),
    ST_LUCIA("LC", "聖露西亞", "St. Lucia", "Santa Lúcia"),
    DINAMARCA("DK", "丹麥", "Dinamarca", "Dinamarca"),
    URUGUAY("UY", "烏拉圭", "Uruguay", "Uruguai"),
    SUICA("CH", "瑞士", "Suica", "Suíça"),
    POLANDA("PL", "波蘭", "Polanda", "Polónia"),
    GRANDOLA("GD", "格林納達", "Grandola", "Granada"),
    JUGOSLAVIA("YU", "南斯拉夫", "Jugoslavia", "Jugoslávia"),
    CHILE("CL", "智利", "CHILE", "Chile"),
    TUNISIA("TN", "突尼斯", "Tunisia", "Tunísia"),
    NAURU("NR", "諾魯/瑙魯", "Nauru", "Nauru"),
    CABO_VERDE("CV", "佛得角", "Cabo Verde", "Cabo Verde"),
    SAO_TOME_AND_PRINCIPE("ST", "聖多美和林西比", "São Tomé and Príncipe", "São Tomé e Príncipe"),
    NAPEL("NP", "尼泊爾", "Napel", "Nepal"),
    HUNGRIA("HU", "匈牙利", "HUNGRIA", "Hungria"),
    JAMAICA("JM", "牙買加", "Jamaica", "Jamaica"),
    FINLAND("FI", "芬蘭", "Finland", "Finlândia"),
    BUGARLIA("BG", "保加利亞", "Bugarlia", "Bulgária"),
    TURKIA("TR", "土耳其", "Turkia", "Turquia"),
    NIGERIA("NG", "尼日利亞", "NIGERIA", "Nigéria"),
    ARABES_UNIDOS_ESTADOS_DOS_EMIRADOS("AE", "阿拉伯聯合酋長國", "Arabes Unidos (Estados dos Emirados)", "Emirados Árabes Unidos"),
    TOGO("TG", "多哥", "Togo", "Togo"),
    ROMANIA("RO", "羅馬尼亞", "Romania", "Roménia"),
    UKRAINE("UA", "烏克蘭", "Ukraine", "Ucrânia"),
    GREECE("GR", "希臘", "Greece", "Grécia"),
    REPUBLIC_OF_ARMENIA("AM", "亞美尼亞", "REPUBLIC OF ARMENIA", "República da Arménia"),
    DUBAI("DXB", "杜拜", "DUBAI", "Dubai"),
    MALDIVAS("MV", "馬爾代夫", "Maldivas", "Maldivas"),
    SIRIA("SY", "敘利亞", "Siria", "Síria"),
    MARSHALL_ISLANDS("MH", "馬紹爾群島", "MARSHALL ISLANDS", "Ilhas Marshall"),
    MAJURO("MAJ", "馬朱羅", "MAJURO", "Majuro"),
    CZECK_REPUBLIC("CZ", "捷克", "CZECK REPUBLIC", "República Checa"),
    INCONHECIDO("XX", "不詳", "INCONHECIDO", "Desconhecido");

    @Getter
    private final String code;
    @Getter
    private final String zhTwValue;
    @Getter
    private final String enValue;
    @Getter
    private final String ptValue;

    BirthPlaceEnum(String code, String zhTwValue, String enValue, String ptValue) {
        this.code = code;
        this.zhTwValue = zhTwValue;
        this.enValue = enValue;
        this.ptValue = ptValue;
    }

    public static String getValue(String code, SchoolLanguageEnum language) {
        if (StringUtils.isNotBlank(code)) {
            for (BirthPlaceEnum place : values()) {
                if (place.code.equals(code)) {
                    switch (language) {
                        case EN_US:
                            return place.enValue;
                        case PT_PT:
                            return place.ptValue;
                        default:
                            return place.zhTwValue;
                    }
                }
            }
        }
        return "";
    }

    public static String getCode(String value, SchoolLanguageEnum language) {
        if (StringUtils.isNotBlank(value)) {
            for (BirthPlaceEnum place : values()) {
                switch (language) {
                    case EN_US:
                        if (place.enValue.equals(value)) return place.code;
                        break;
                    case PT_PT:
                        if (place.ptValue.equals(value)) return place.code;
                        break;
                    default:
                        if (place.zhTwValue.equals(value)) return place.code;
                }
            }
        }
        return null;
    }
}