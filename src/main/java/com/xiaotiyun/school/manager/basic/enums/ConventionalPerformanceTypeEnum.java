package com.xiaotiyun.school.manager.basic.enums;

import lombok.Getter;

public enum ConventionalPerformanceTypeEnum {
    CLASS_VIOLATION(1, "上课违规", "Class Violation", "Violação em Aula"),
    MISSING_HOMEWORK(2, "欠作业", "Missing Homework", "Falta de Tarefa"),
    UNIFORM_NON_COMPLIANCE(3, "仪表不符", "Uniform Non-compliance", "Incompatibilidade de Uniforme"),
    MISSING_TEXTBOOK(5, "欠课本", "Missing Textbook", "Falta de Livro Didático"),
    MISSING_RETURN_STICKER(7, "欠回条", "Missing Return Sticker", "Falta de Boleto de Retorno"),
    ;

    @Getter
    private final int code;
    private final String zhTwValue;
    private final String enValue;
    private final String ptValue;

    ConventionalPerformanceTypeEnum(int code, String zhTwValue, String enValue, String ptValue) {
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
        for (ConventionalPerformanceTypeEnum typeEnum : values()) {
            if (typeEnum.code == code) {
                switch (language) {
                    case EN_US:
                        return typeEnum.enValue;
                    case PT_PT:
                        return typeEnum.ptValue;
                    default:
                        return typeEnum.zhTwValue;
                }
            }
        }
        return "";
    }
}
