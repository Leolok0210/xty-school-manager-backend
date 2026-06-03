package com.xiaotiyun.school.manager.basic.enums;

public enum ExternalCompetitionAreaEnum {

    CAMPUS("校内", "Campus", "O campus", 1),
    HK_MC("港澳区", "Hong Kong and Macao Regions", "Hong kong e macau", 2),
    INTERNATIONAL("埠際或國際", "International", "Interportuário ou internacional", 3);

    private String name;
    private String englishName;
    private String ptName;
    private Integer code;

    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getEnglishName() {
        return englishName;
    }

    public String getPtName() {
        return ptName;
    }

    ExternalCompetitionAreaEnum(String name, String englishName, String ptName, Integer code) {
        this.name = name;
        this.englishName = englishName;
        this.ptName = ptName;
        this.code = code;
    }

    public static ExternalCompetitionAreaEnum getByCode(String name) {
        for (ExternalCompetitionAreaEnum value : ExternalCompetitionAreaEnum.values()) {
            if (value.getName().equals(name) ||
                    value.getEnglishName().equals(name) ||
                    value.getPtName().equals(name)) {
                return value;
            }
        }
        return null;
    }

    public static ExternalCompetitionAreaEnum getByCode(Integer code) {
        for (ExternalCompetitionAreaEnum value : ExternalCompetitionAreaEnum.values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return null;
    }
}
