package com.xiaotiyun.school.manager.basic.enums;

import lombok.Getter;

public enum StudentTypeEnum {
    NONE(0, "無", "None", "Nenhum"),
    HUMANITIES(1, "文科生", "Humanities Student", "Letras"),
    SCIENCE(2, "理科生", "Science Student", "Ciências"),
    // 理工科
    SCIENCE_AND_TECHNOLOGY(1, "理工科生", "Engineering and Science Student", "Engenharia e Ciências"),
    //商科
    COMMERCE(3, "商科生", "Commerce Student", "Comercial"),;

    @Getter
    private final int code;
    private final String chinese;
    private final String english;
    private final String portuguese;

    StudentTypeEnum(int code, String chinese, String english, String portuguese) {
        this.code = code;
        this.chinese = chinese;
        this.english = english;
        this.portuguese = portuguese;
    }

    public static int getCodeByLanguage(String currentLanguage, String value) {
        for (StudentTypeEnum type : StudentTypeEnum.values()) {
            if (currentLanguage.equals(SchoolLanguageEnum.ZH_MO.getCode()) && type.chinese.equals(value) ||
                    currentLanguage.equals(SchoolLanguageEnum.EN_US.getCode()) && type.english.equals(value) ||
                    currentLanguage.equals(SchoolLanguageEnum.PT_PT.getCode()) && type.portuguese.equals(value)) {
                return type.code;
            }
        }
        return -1;
    }
}